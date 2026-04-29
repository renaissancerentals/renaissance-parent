Hello RenaissanceRentals Team,

A rental application request has been submitted via ${data.property()}. Details are as follows:

------------------------------------------------------------
Contact Details
------------------------------------------------------------
First Name : ${data.firstName()!""}
Last Name : ${data.lastName()!""}
Email : ${data.email()!""}
<#if data.phone()?? && data.phone()?has_content>
Phone : ${data.phone()}
</#if>

------------------------------------------------------------
Message
------------------------------------------------------------
${data.questions()!""}

------------------------------------------------------------
Additional Information
------------------------------------------------------------
Interested Community : ${data.community()!""}
<#if data.address()?? && data.address()?has_content>
Interested Location :${data.address()}
</#if>
<#if data.currentPage()?? && data.currentPage()?has_content>
Submitted From :
    ${data.currentPage()}
</#if>

------------------------------------------------------------
Action
------------------------------------------------------------
Reply directly to this email to contact the sender.

Best regards,
RenaissanceRentals Website
