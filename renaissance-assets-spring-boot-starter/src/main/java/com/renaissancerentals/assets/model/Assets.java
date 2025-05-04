package com.renaissancerentals.assets.model;

import java.util.List;

public record Assets(List<Asset> assets, String nextPageToken) {
    public Assets {
        assets = assets == null ? List.of() : List.copyOf(assets);
    }
}
