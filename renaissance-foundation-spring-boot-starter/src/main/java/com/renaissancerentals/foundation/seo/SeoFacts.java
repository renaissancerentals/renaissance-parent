package com.renaissancerentals.foundation.seo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Turns stored codes (pet policy, lease type, floor level, ...) into plain sentences for people and crawlers. */
final class SeoFacts {

    private SeoFacts() {}

    /** CAT, SMALL_DOG_CAT, LARGE_DOG_SMALL_DOG_CAT, NO_PET. Null when the data says nothing. */
    static String pets(String allowedPet) {
        if (SeoText.blank(allowedPet)) {
            return null;
        }
        var code = allowedPet.trim().toUpperCase(Locale.ROOT);
        if (code.equals("NO_PET") || code.equals("NONE")) {
            return "No pets allowed";
        }
        var kinds = new ArrayList<String>();
        if (code.contains("CAT")) kinds.add("cats");
        if (code.contains("SMALL_DOG")) kinds.add("small dogs");
        if (code.contains("LARGE_DOG")) kinds.add("large dogs");
        return kinds.isEmpty() ? null : "Pets allowed: " + join(kinds);
    }

    static String lease(String leaseType) {
        if (SeoText.blank(leaseType)) {
            return null;
        }
        return switch (leaseType.trim().toUpperCase(Locale.ROOT)) {
            case "YEARLY" -> "Yearly leases";
            case "SHORT_TERM" -> "Short term leases";
            default -> null;
        };
    }

    static String level(String level) {
        if (SeoText.blank(level)) {
            return null;
        }
        return switch (level.trim().toUpperCase(Locale.ROOT)) {
            case "GROUND" -> "Ground floor";
            case "MIDDLE" -> "Middle floor";
            case "TOP" -> "Top floor";
            case "MULTI" -> "Multiple levels";
            default -> null;
        };
    }

    /** ADDRESS_SERVICES style codes and enum names to "Smart living". */
    static String heading(String type) {
        return switch (type == null ? "" : type.trim().toUpperCase(Locale.ROOT)) {
            case "AMENITIES" -> "Amenities";
            case "SERVICES" -> "Services";
            case "SMART_LIVING" -> "Smart living";
            default -> SeoText.blank(type) ? "Amenities" : capitalise(type.replace('_', ' '));
        };
    }

    static String humanise(String code) {
        return SeoText.blank(code) ? null : capitalise(code.replace('_', ' ').toLowerCase(Locale.ROOT));
    }

    static String money(Number amount) {
        return "$" + String.format(Locale.ROOT, "%,d", Math.round(amount.doubleValue()));
    }

    static String join(List<String> items) {
        if (items.size() <= 1) {
            return String.join("", items);
        }
        return String.join(", ", items.subList(0, items.size() - 1)) + " and " + items.get(items.size() - 1);
    }

    private static String capitalise(String text) {
        return text.isEmpty() ? text : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /** Utilities the rent covers, taken from INCLUDED_UTILITY entries. */
    static List<String> includedUtilities(List<SeoData.Utility> utilities) {
        return utilities.stream()
                .filter(u -> "INCLUDED_UTILITY".equalsIgnoreCase(u.type()) && !SeoText.blank(u.name()))
                .map(u -> SeoText.plain(u.name()))
                .toList();
    }

    /** Utilities the resident pays, with the typical monthly bill when known. */
    static List<String> residentUtilities(List<SeoData.Utility> utilities) {
        return utilities.stream()
                .filter(u -> "RESIDENT_UTILITY".equalsIgnoreCase(u.type()) && !SeoText.blank(u.name()))
                .map(u -> SeoText.plain(u.name())
                        + (u.averageMonthlyBill() != null && u.averageMonthlyBill() > 0
                                ? " (about " + money(u.averageMonthlyBill()) + " per month)"
                                : ""))
                .toList();
    }
}
