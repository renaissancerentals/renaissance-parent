Hello RenaissanceRentals Team,

A utility setup confirmation has been submitted for:
    ${data.unitAddress}.

Details are as follows:

------------------------------------------------------------
Resident Details
------------------------------------------------------------
Name : ${data.name!""}
Email : ${data.email!""}

------------------------------------------------------------
Utility Information
------------------------------------------------------------
Unit ID: ${data.unitId!""}
Unit Address: ${data.unitAddress!""}
Account Numbers:
<#if data.vectrenAccountNumber?? && data.vectrenAccountNumber?has_content>
    Vectren :${data.vectrenAccountNumber}
</#if>
<#if data.dukeAccountNumber?? && data.dukeAccountNumber?has_content>
    Duke :${data.dukeAccountNumber}
</#if>
<#if data.waterFormSubmitted!false>
    CBU (Water): Online form has been submitted : YES
</#if>

------------------------------------------------------------
Action
------------------------------------------------------------
Reply directly to this email to contact the sender.

Best regards,
RenaissanceRentals Website
