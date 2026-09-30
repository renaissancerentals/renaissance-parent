package com.renaissancerentals.foundation.seo;

import java.util.regex.Pattern;
import org.springframework.web.util.HtmlUtils;

/** Text clean-up helpers for titles and descriptions. */
final class SeoText {

    static final int MAX_TITLE = 70;
    static final int MAX_DESCRIPTION = 160;

    private static final Pattern TAGS = Pattern.compile("<[^>]*>");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s\\u00A0]+");

    private SeoText() {}

    static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    /** Removes markup and entities, collapses whitespace. Never returns null. */
    static String plain(String value) {
        if (value == null) {
            return "";
        }
        var noTags = TAGS.matcher(value).replaceAll(" ");
        return WHITESPACE
                .matcher(HtmlUtils.htmlUnescape(noTags))
                .replaceAll(" ")
                .trim();
    }

    /** Clips to {@code max} characters on a word boundary, adding an ellipsis when something was cut. */
    static String clip(String value, int max) {
        var text = plain(value);
        if (text.length() <= max) {
            return text;
        }
        var cut = text.substring(0, max - 1);
        var space = cut.lastIndexOf(' ');
        if (space > max / 2) {
            cut = cut.substring(0, space);
        }
        return cut.replaceAll("[\\s,;:.\\-]+$", "") + "…";
    }

    static String description(String preferred, String fallback) {
        var text = blank(plain(preferred)) ? plain(fallback) : plain(preferred);
        return clip(text, MAX_DESCRIPTION);
    }

    /** Uses the stored title when it is sensible, otherwise the generated one. */
    static String title(String preferred, String generated) {
        var text = plain(preferred);
        return !text.isEmpty() && text.length() <= MAX_TITLE ? text : clip(generated, MAX_TITLE);
    }

    static String esc(String value) {
        return HtmlUtils.htmlEscape(value == null ? "" : value, "UTF-8");
    }

    /** 8123332280 becomes +1-812-333-2280; anything else is returned as is. */
    static String phone(String value) {
        if (blank(value)) {
            return null;
        }
        var digits = value.replaceAll("\\D", "");
        if (digits.length() == 11 && digits.startsWith("1")) {
            digits = digits.substring(1);
        }
        return digits.length() == 10
                ? "+1-" + digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6)
                : value.trim();
    }
}
