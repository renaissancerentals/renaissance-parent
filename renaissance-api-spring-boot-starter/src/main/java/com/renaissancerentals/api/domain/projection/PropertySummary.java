package com.renaissancerentals.api.domain.projection;

import com.renaissancerentals.api.domain.PropertyBusRoute;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@SuppressFBWarnings(
        value = "EI_EXPOSE_REP",
        justification = "DTO used only for serialization; safe to expose collections")
public class PropertySummary {
    private String id;

    private String name;

    private String address;

    private String zipcode;

    private String email;

    private String phone;

    private String leaseType;

    private List<PropertyBusRoute> busRoutes;
}
