package com.renaissancerentals.api.domain.projection;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "DTO used only for serialization; safe to expose collections")
public class FloorplanSpotlight {
    private String id;
    private String name;
    private String metaDescription;
    private Integer bedroom;
    private Float bathroom;
    private String style;
    private String coverImage;
    private List<UnitSpotlight> units;
    private PropertySpotlight property;
}
