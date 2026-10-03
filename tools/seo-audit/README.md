# SEO / GEO audit

Re-runnable check of all Renaissance sites, standard library Python only.

```bash
python3 tools/seo-audit/audit.py                    # all sites, 25 pages each from their sitemap
python3 tools/seo-audit/audit.py --site high-grove  # one site by name, or pass any https URL
python3 tools/seo-audit/audit.py --lighthouse       # also Lighthouse per home page (needs Node and Chrome, slow)
python3 tools/seo-audit/audit.py --out report.md    # Markdown report with every finding
python3 -m unittest -q                              # tests for the checks (run from this folder)
```

Scores are 0-100 per module: **Indexing** (robots.txt, sitemap, canonical, 404s), **Metadata** (title 30-60,
description 120-160), **Structured Data** (JSON-LD parses and has the expected schema.org type), **Social Preview**
(Open Graph, Twitter), **Content** (one h1, crawler snapshot, llms.txt), **Security** (https). FAIL scores 0, WARN
half. Exit code 1 means at least one FAIL, so it can gate a deploy. Pages in the sitemap must be self-canonical
and indexable.

It does not reproduce the Nurture Boss score, but covers the same findings. Edit `SITES` at the top of `audit.py`
when a site is added.
