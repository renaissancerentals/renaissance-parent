package com.renaissancerentals.api.repository.mapper;

import com.renaissancerentals.api.domain.LeasingOffice;
import com.renaissancerentals.api.domain.projection.PropertyDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class PropertyDetailsJdbcMapper implements RowMapper<PropertyDetails> {
    @Nullable
    @Override
    public PropertyDetails mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        return PropertyDetails.builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .address(rs.getString("address"))
                .zipcode(rs.getString("zipcode"))
                .email(rs.getString("email"))
                .secondaryEmail(rs.getString("secondary_email"))
                .phone(rs.getString("phone"))
                .rating(rs.getFloat("rating"))
                .facebookLink(rs.getString("facebook_link"))
                .twitterLink(rs.getString("twitter_link"))
                .logo(rs.getString("logo"))
                .coverImage(rs.getString("cover_image"))
                .coverVideo(rs.getString("cover_video"))
                .propertyFolderId(rs.getString("property_folder_id"))
                .photosFolderId(rs.getString("photos_folder_id"))
                .youtubeLink(rs.getString("youtube_link"))
                .description(rs.getString("description"))
                .ratingLink(rs.getString("rating_link"))
                .htmlTitle(rs.getString("html_title"))
                .metaDescription(rs.getString("meta_description"))
                .conversionTrackingId1(rs.getString("conversion_tracking_id1"))
                .conversionTrackingId2(rs.getString("conversion_tracking_id2"))
                .leaseType(rs.getString("lease_type"))
                .leasingOfficeType(rs.getString("leasing_office_type"))
                .leasingOffice(LeasingOffice.builder()
                        .id(rs.getString("leasingOfficeId"))
                        .name(rs.getString("leasingOfficeName"))
                        .address(rs.getString("leasingOfficeAddress"))
                        .zipcode(rs.getString("leasingOfficeZipcode"))
                        .phone(rs.getString("leasingOfficePhone"))
                        .officeHours(rs.getString("leasingOfficeHours"))
                        .direction(rs.getString("leasingOfficeDirection"))
                        .officeMap(rs.getString("leasingOfficeMap"))
                        .officeMapLandscape(rs.getString("leasingOfficeMapLandscape"))
                        .officeImage(rs.getString("leasingOfficeImage"))
                        .officeImageDescription(rs.getString("leasingOfficeImageDescription"))
                        .build())
                .build();
    }
}
