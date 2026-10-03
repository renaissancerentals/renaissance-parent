#!/usr/bin/env python3
"""SEO / GEO audit for the Renaissance sites. Standard library only; Lighthouse is optional (needs Node + Chrome).

    python3 tools/seo-audit/audit.py                       # all sites, up to 25 pages each
    python3 tools/seo-audit/audit.py --site high-grove     # one site by name, or any URL
    python3 tools/seo-audit/audit.py --lighthouse          # also run Lighthouse on each home page
    python3 tools/seo-audit/audit.py --out report.md       # write a Markdown report

Exit code is 1 when any check FAILs, so it can gate a deploy. WARN means "outside the recommended range".
"""
import argparse
import concurrent.futures as futures
import html
import json
import os
import re
import subprocess
import sys
import urllib.error
import urllib.request
from html.parser import HTMLParser
from urllib.parse import urlparse

SITES = {
    "hub": ("https://www.renaissancerentals.com", None),
    "covenanter-hill": ("https://www.covenanterhill.com", "covenanter-hill"),
    "high-grove": ("https://www.highgrovebloomington.com", "high-grove"),
    "scholars-quad": ("https://www.scholarsquad.com", "scholars-quad"),
    "scholars-rooftop": ("https://www.scholarsrooftop.com", "scholars-rooftop"),
    "summer-house": ("https://www.summerhouseatindiana.com", "summer-house"),
    "verona-park": ("https://www.veronaparkneighborhood.com", "verona-park"),
    "aib": ("https://www.apartmentsinbloomington.com", None),
    "baan": ("https://www.bloomingtonapartmentsavailablenow.com", None),
}
MODULES = ["Indexing", "Metadata", "Structured Data", "Social Preview", "Content", "Security"]
PASS, WARN, FAIL = "PASS", "WARN", "FAIL"
SCORE = {PASS: 1.0, WARN: 0.5, FAIL: 0.0}
TITLE_OK, TITLE_WARN = (30, 60), (20, 70)
DESC_OK, DESC_WARN = (120, 160), (70, 180)
USER_AGENT = "Mozilla/5.0 (compatible; renaissance-seo-audit)"


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, *args, **kwargs):
        return None


_opener = urllib.request.build_opener(NoRedirect)


def fetch(url, timeout=30):
    """(status, body, headers). Never raises; status 0 means the request itself failed."""
    try:
        response = _opener.open(urllib.request.Request(url, headers={"User-Agent": USER_AGENT}), timeout=timeout)
        return response.status, response.read().decode("utf-8", "replace"), response.headers
    except urllib.error.HTTPError as e:
        return e.code, e.read().decode("utf-8", "replace"), e.headers
    except Exception as e:  # noqa: BLE001 - report any network failure as status 0
        return 0, str(e), {}


class PageParser(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.titles, self.meta, self.canonicals, self.ld, self.h1 = [], [], [], [], 0
        self.snapshot = False
        self._in_title = False
        self._in_ld = False
        self._buf = ""

    def handle_starttag(self, tag, attrs):
        a = dict(attrs)
        if tag == "title":
            self._in_title, self._buf = True, ""
        elif tag == "meta" and (a.get("name") or a.get("property")):
            self.meta.append((a.get("name") or a.get("property"), a.get("content", "")))
        elif tag == "link" and a.get("rel") == "canonical":
            self.canonicals.append(a.get("href", ""))
        elif tag == "h1":
            self.h1 += 1
        elif tag == "script" and a.get("type") == "application/ld+json":
            self._in_ld, self._buf = True, ""
        if "data-seo-snapshot" in a:
            self.snapshot = True

    def handle_data(self, data):
        if self._in_title or self._in_ld:
            self._buf += data

    def handle_endtag(self, tag):
        if tag == "title" and self._in_title:
            self.titles.append(self._buf.strip())
            self._in_title = False
        elif tag == "script" and self._in_ld:
            self.ld.append(self._buf)
            self._in_ld = False

    def values(self, name):
        return [v for k, v in self.meta if k == name]


def ld_types(blocks):
    """(types, problems) from JSON-LD script bodies, flattening @graph."""
    types, problems = set(), []
    for block in blocks:
        try:
            data = json.loads(block)
        except ValueError:
            problems.append("invalid JSON")
            continue
        for node in data.get("@graph", [data]) if isinstance(data, dict) else data:
            kind = node.get("@type")
            types |= set(kind if isinstance(kind, list) else [kind]) if kind else set()
    return types, problems


def expected_types(owned, path):
    """The schema.org types a page should carry (any one of them)."""
    if path == "/":
        return {"ApartmentComplex"} if owned else {"WebSite"}
    if re.fullmatch(r"/(floorplans|units)/[^/]+", path):
        return {"FloorPlan"}
    if re.fullmatch(r"/properties/[^/]+", path):
        return {"ApartmentComplex"}
    if re.fullmatch(r"/employment/\d+", path):
        return {"JobPosting"}
    if re.fullmatch(r"/sublets/[^/]+", path):
        return set()  # no schema.org type is emitted for sublets
    return {"WebPage", "CollectionPage"}


def in_range(n, ok, warn):
    if ok[0] <= n <= ok[1]:
        return PASS
    return WARN if warn[0] <= n <= warn[1] else FAIL


def analyze(url, status, body, headers, owned, in_sitemap):
    """Checks for one page: a list of (module, level, message)."""
    path = urlparse(url).path or "/"
    r = []
    add = lambda module, level, msg: r.append((module, level, msg))  # noqa: E731
    if status != 200:
        add("Indexing", FAIL, f"status {status}")
        return r
    p = PageParser()
    p.feed(body)

    # Metadata
    if len(p.titles) != 1:
        add("Metadata", FAIL, f"{len(p.titles)} <title> tags")
    else:
        n = len(p.titles[0])
        add("Metadata", in_range(n, TITLE_OK, TITLE_WARN), f"title {n} chars: {p.titles[0][:60]}")
    descriptions = p.values("description")
    if len(descriptions) != 1:
        add("Metadata", FAIL, f"{len(descriptions)} meta descriptions")
    else:
        n = len(descriptions[0])
        add("Metadata", in_range(n, DESC_OK, DESC_WARN), f"description {n} chars")

    # Indexing
    if len(p.canonicals) != 1:
        add("Indexing", FAIL, f"{len(p.canonicals)} canonical links")
    elif in_sitemap and p.canonicals[0] != url:
        add("Indexing", FAIL, f"in sitemap but canonical is {p.canonicals[0]}")
    else:
        add("Indexing", PASS, f"canonical {p.canonicals[0]}")
    robots = " ".join(p.values("robots")).lower()
    if in_sitemap and "noindex" in robots:
        add("Indexing", FAIL, "in sitemap but noindex")

    # Social preview
    for tag in ("og:title", "og:description", "og:url", "twitter:card"):
        if not p.values(tag):
            add("Social Preview", FAIL, f"missing {tag}")
    if not p.values("og:image"):
        add("Social Preview", WARN, "missing og:image")
    if not any(m == "Social Preview" for m, _, _ in r):
        add("Social Preview", PASS, "open graph and twitter tags present")

    # Structured data
    types, problems = ld_types(p.ld)
    want = expected_types(owned, path) if in_sitemap else set()
    if problems:
        add("Structured Data", FAIL, "; ".join(problems))
    elif want and not (want & types):
        add("Structured Data", FAIL, f"expected one of {sorted(want)}, found {sorted(types) or 'none'}")
    else:
        add("Structured Data", PASS, f"types {sorted(types) or 'none needed'}")

    # Content
    if p.h1 != 1:
        add("Content", WARN, f"{p.h1} <h1> elements in the initial HTML")
    else:
        add("Content", PASS, "one <h1>")
    add("Content", PASS if p.snapshot else WARN, "crawler snapshot " + ("present" if p.snapshot else "missing"))

    # Security
    if not url.startswith("https://"):
        add("Security", FAIL, "not served over https")
    else:
        add("Security", PASS, "https")
    return r


def audit_site(name, origin, owned, max_pages):
    findings = []  # (path, module, level, message)

    def site_level(module, level, msg):
        findings.append(("(site)", module, level, msg))

    status, robots, _ = fetch(origin + "/robots.txt")
    if status != 200 or f"Sitemap: {origin}/sitemap.xml" not in robots:
        site_level("Indexing", FAIL, "robots.txt missing or does not point at this site's sitemap")
    else:
        site_level("Indexing", PASS, "robots.txt points at the sitemap")
    status, sitemap, _ = fetch(origin + "/sitemap.xml")
    locs = re.findall(r"<loc>([^<]+)</loc>", html.unescape(sitemap)) if status == 200 else []
    if not locs:
        site_level("Indexing", FAIL, f"sitemap.xml empty or unavailable (status {status})")
    elif any(not loc.startswith(origin + "/") and loc != origin for loc in locs):
        site_level("Indexing", FAIL, "sitemap lists URLs on another host")
    else:
        site_level("Indexing", PASS, f"sitemap lists {len(locs)} URLs on this host")
    status, _, _ = fetch(origin + "/llms.txt")
    site_level("Content", PASS if status == 200 else WARN, f"llms.txt status {status}")
    status, _, _ = fetch(origin + "/__audit-not-found")
    site_level("Indexing", PASS if status == 404 else FAIL, f"unknown URL answers {status} (want 404)")

    sample = pick_sample(locs, max_pages, origin)
    with futures.ThreadPoolExecutor(max_workers=8) as pool:
        pages = list(pool.map(lambda u: (u,) + fetch(u), sample))
    for url, status, body, headers in pages:
        path = urlparse(url).path or "/"
        for module, level, msg in analyze(url, status, body, headers, owned, in_sitemap=url in locs):
            findings.append((path, module, level, msg))
    return findings, len(locs), len(sample)


def pick_sample(locs, limit, origin):
    """Home page first, then an even spread over the first path segments (floorplans, units, ...)."""
    groups = {}
    for loc in locs:
        groups.setdefault((urlparse(loc).path.strip("/").split("/") + [""])[0], []).append(loc)
    sample = [origin + "/"]
    queues = [list(v) for v in groups.values()]
    while len(sample) < limit and any(queues):
        for q in queues:
            if q and len(sample) < limit:
                url = q.pop(0)
                if url not in sample:
                    sample.append(url)
    return sample


def module_scores(findings):
    scores = {}
    for module in MODULES:
        levels = [lv for _, m, lv, _ in findings if m == module]
        scores[module] = round(100 * sum(SCORE[lv] for lv in levels) / len(levels)) if levels else None
    return scores


def lighthouse(url):
    chrome = os.environ.get("CHROME_PATH", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome")
    out = f"/tmp/seo-audit-lh-{abs(hash(url))}.json"
    cmd = ["npx", "--yes", "lighthouse@12", url, "--quiet", "--chrome-flags=--headless=new --no-sandbox",
           "--only-categories=performance,seo,best-practices,accessibility", "--output=json", f"--output-path={out}"]
    try:
        subprocess.run(cmd, env={**os.environ, "CHROME_PATH": chrome}, timeout=420, check=True, capture_output=True)
        with open(out) as f:
            data = json.load(f)
        return {k: round(v["score"] * 100) for k, v in data["categories"].items()}
    except Exception as e:  # noqa: BLE001
        return {"error": str(e)[:80]}


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--site", action="append", help="site name (see SITES) or a URL; repeatable. Default: all")
    ap.add_argument("--max-pages", type=int, default=25, help="pages sampled from each sitemap (default 25)")
    ap.add_argument("--lighthouse", action="store_true", help="also run Lighthouse on each home page (slow)")
    ap.add_argument("--out", help="write a Markdown report to this path")
    args = ap.parse_args(argv)

    chosen = {}
    for s in args.site or SITES:
        if s in SITES:
            chosen[s] = SITES[s]
        elif s.startswith("http"):
            chosen[urlparse(s).netloc] = (s.rstrip("/"), None)
        else:
            sys.exit(f"unknown site {s!r}; known: {', '.join(SITES)}")

    lines, failed = ["# SEO / GEO audit", ""], False
    header = f"{'site':18}" + "".join(f"{m[:11]:>13}" for m in MODULES) + "   pages  fails warns"
    print(header)
    lines += ["| site | " + " | ".join(MODULES) + " | pages | fails | warns |", "|---|" + "---|" * (len(MODULES) + 3)]
    details = []
    for name, (origin, owned) in chosen.items():
        findings, total, sampled = audit_site(name, origin, owned, args.max_pages)
        scores = module_scores(findings)
        fails = [f for f in findings if f[2] == FAIL]
        warns = [f for f in findings if f[2] == WARN]
        failed |= bool(fails)
        cells = [("-" if scores[m] is None else str(scores[m])) for m in MODULES]
        print(f"{name:18}" + "".join(f"{c:>13}" for c in cells) + f"   {sampled}/{total}  {len(fails):>4} {len(warns):>5}")
        lines.append(f"| {name} | " + " | ".join(cells) + f" | {sampled}/{total} | {len(fails)} | {len(warns)} |")
        if fails or warns:
            details.append(f"\n## {name}\n")
            for path, module, level, msg in sorted(fails + warns, key=lambda f: (f[2] != FAIL, f[0])):
                details.append(f"- **{level}** `{path}` [{module}] {msg}")
        if args.lighthouse:
            result = lighthouse(origin + "/")
            print(f"{'':18}lighthouse: {result}")
            details.append(f"\n**Lighthouse {name}**: {result}\n")
    print()
    for d in details:
        print(d)
    if args.out:
        with open(args.out, "w") as f:
            f.write("\n".join(lines + details) + "\n")
        print(f"\nreport written to {args.out}")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
