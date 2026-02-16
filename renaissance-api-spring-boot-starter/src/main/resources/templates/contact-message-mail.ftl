Hello RenaissanceRentals Team,

A new message has been submitted via ${data.property}. Details are as follows:

------------------------------------------------------------
Contact Details
------------------------------------------------------------
Name : ${data.name!""}
Email : ${data.email!""}
Phone : ${data.phone!""}

<#assign methods = []>
<#if data.emailPreferred?? && data.emailPreferred>
    <#assign methods = methods + ["Email"]>
</#if>
<#if data.phonePreferred?? && data.phonePreferred>
    <#assign methods = methods + ["Phone"]>
</#if>
<#if data.textPreferred?? && data.textPreferred>
    <#assign methods = methods + ["Text"]>
</#if>

<#if methods?size gt 0>
Preferred : ${methods?join(", ")}
</#if>

<#if data.currentPage?? && data.currentPage?has_content>
Submitted From :
    ${data.currentPage}
</#if>

------------------------------------------------------------
Message
------------------------------------------------------------
${data.question!""}


<#if data.additionalInfo??>
------------------------------------------------------------
Additional Information
------------------------------------------------------------
Price Range : $${data.additionalInfo.lowerRent!""} – $${data.additionalInfo.upperRent!""}

    <#if data.additionalInfo.bedrooms?? && data.additionalInfo.bedrooms != "0">
Bedrooms : ${data.additionalInfo.bedrooms}
    </#if>

    <#if data.additionalInfo.moveInDate?? && data.additionalInfo.moveInDate?has_content>
Move-In : ${data.additionalInfo.moveInDate}
    </#if>

    <#if data.additionalInfo.amenities?? && data.additionalInfo.amenities?has_content>
Amenities :
        ${data.additionalInfo.amenities}
    </#if>

    <#if data.additionalInfo.pets?? && data.additionalInfo.pets?has_content>
Pets : ${data.additionalInfo.pets}
    </#if>

    <#if data.additionalInfo.floorPlan?? && data.additionalInfo.floorPlan?has_content>
Floor Plan : ${data.additionalInfo.floorPlan}
    </#if>

    <#if data.additionalInfocommunities?? && data.additionalInfocommunities?has_content>
Community : ${data.additionalInfocommunities}
    </#if>

    <#if data.additionalInfo.hearAboutUs?? && data.additionalInfo.hearAboutUs?has_content>
Heard Via : ${data.additionalInfo.hearAboutUs}
    </#if>
</#if>

------------------------------------------------------------
Action
------------------------------------------------------------
Reply directly to this email to contact the sender.

Best regards,
RenaissanceRentals Website
