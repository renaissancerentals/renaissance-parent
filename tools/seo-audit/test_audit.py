import unittest

import audit

GOOD = """<html><head><title>{title}</title>
<meta name="description" content="{desc}"/>
<link rel="canonical" href="{canonical}"/>
<meta name="robots" content="index, follow"/>
<meta property="og:title" content="t"/><meta property="og:description" content="d"/>
<meta property="og:url" content="u"/><meta property="og:image" content="i"/>
<meta name="twitter:card" content="summary"/>
<script type="application/ld+json">{ld}</script></head>
<body><div id="root"><div data-seo-snapshot hidden><h1>Hi</h1></div></div></body></html>"""
URL = "https://x.test/floorplans/a"
TITLE = "T" * 50
DESC = "D" * 140
LD = '{"@context":"https://schema.org","@graph":[{"@type":"FloorPlan"},{"@type":"BreadcrumbList"}]}'


def page(**kw):
    values = dict(title=TITLE, desc=DESC, canonical=URL, ld=LD)
    values.update(kw)
    return GOOD.format(**values)


def levels(results, module):
    return [lv for m, lv, _ in results if m == module]


class AnalyzeTest(unittest.TestCase):
    def run_checks(self, body, status=200, in_sitemap=True, url=URL):
        return audit.analyze(url, status, body, {}, None, in_sitemap)

    def test_a_good_page_has_no_failures_or_warnings(self):
        results = self.run_checks(page())
        self.assertEqual([r for r in results if r[1] != audit.PASS], [])

    def test_missing_canonical_fails(self):
        body = page().replace(f'<link rel="canonical" href="{URL}"/>', "")
        self.assertIn(audit.FAIL, levels(self.run_checks(body), "Indexing"))

    def test_sitemap_page_must_be_self_canonical(self):
        results = self.run_checks(page(canonical="https://other.test/x"))
        self.assertIn(audit.FAIL, levels(results, "Indexing"))
        self.assertNotIn(audit.FAIL, levels(self.run_checks(page(canonical="https://other.test/x"), in_sitemap=False), "Indexing"))

    def test_title_and_description_ranges(self):
        self.assertIn(audit.FAIL, levels(self.run_checks(page(title="Short")), "Metadata"))
        self.assertIn(audit.WARN, levels(self.run_checks(page(title="T" * 65)), "Metadata"))
        self.assertIn(audit.WARN, levels(self.run_checks(page(desc="D" * 100)), "Metadata"))

    def test_invalid_json_ld_fails_and_wrong_type_fails(self):
        self.assertIn(audit.FAIL, levels(self.run_checks(page(ld="{not json")), "Structured Data"))
        wrong = '{"@context":"https://schema.org","@type":"WebSite"}'
        self.assertIn(audit.FAIL, levels(self.run_checks(page(ld=wrong)), "Structured Data"))

    def test_non_200_is_a_failure(self):
        self.assertEqual(self.run_checks("", status=500), [("Indexing", audit.FAIL, "status 500")])

    def test_noindex_page_in_sitemap_fails(self):
        body = page().replace("index, follow", "noindex, follow")
        self.assertIn(audit.FAIL, levels(self.run_checks(body), "Indexing"))

    def test_expected_types(self):
        self.assertEqual(audit.expected_types("high-grove", "/"), {"ApartmentComplex"})
        self.assertEqual(audit.expected_types(None, "/"), {"WebSite"})
        self.assertEqual(audit.expected_types(None, "/units/u1"), {"FloorPlan"})
        self.assertEqual(audit.expected_types(None, "/sublets/k"), set())
        self.assertIn("WebPage", audit.expected_types(None, "/apply"))

    def test_sample_is_spread_across_sections(self):
        locs = [f"https://x.test/units/{i}" for i in range(50)] + ["https://x.test/floorplans/a", "https://x.test/apply"]
        sample = audit.pick_sample(locs, 6, "https://x.test")
        self.assertEqual(sample[0], "https://x.test/")
        self.assertIn("https://x.test/floorplans/a", sample)
        self.assertIn("https://x.test/apply", sample)


if __name__ == "__main__":
    unittest.main()
