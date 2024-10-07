<%
    ui.decorateWith("appui", "standardEmrPage")

    def insuranceOptions = []
    insurances.each {
        insuranceOptions.push([ label: ui.format(it.name), value: it.insuranceId ])
    }
    insuranceOptions = insuranceOptions.sort { it.label }

    def thirdPartyOptions = []
    thirdParties.each {
        thirdPartyOptions.push([ label: ui.format(it.name), value: it.thirdPartyId ])
    }
    thirdPartyOptions = thirdPartyOptions.sort { it.label }

    def ownerOptions = []
    owners.each {
        ownerOptions.push([ label: ui.format(it), value: it.id ])
    }
    ownerOptions = ownerOptions.sort { it.label }

    def levelOptions = [[label: "1", value: 1],[label: "2", value: 2],[label: "3", value: 3],[label: "4", value: 4]]
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<style>
    div[data-lastpass-icon-root] {
        display: none;
    }
    #insurance-policy-form fieldset {
        margin-bottom: 0;
        padding-bottom: 0;
        padding-top: 5px;
        width: 100%;
        & legend {
            font-size: 1.2rem;
            font-weight: bold;
            padding: 0;
            margin-bottom: 0;
        }
    }
    #insurance-policy-form td {
        width: 50%;
        vertical-align: top;
    }
</style>

<h3>${ ui.message(policy.policyId == null ? "mohbilling.insurance.policy.create" : "mohbilling.insurance.policy.edit") }</h3>
<br/>
<form method="post" id="insurance-policy-form">
    <input type="hidden" value="${ui.format(policy.policyId)}" name="policyId" />
    <input type="hidden" value="${patient.patient.id}" name="patientId" />

    <table style="width:100%"><tr>
        <td>
            <fieldset>
                <legend>${ui.message("imbemr.insurance.owner")}</legend>

                ${ ui.includeFragment("uicommons", "field/dropDown", [
                        label: ui.message("imbemr.patientName"),
                        hideEmptyLabel: true,
                        formFieldName: "owner",
                        initialValue: (policy.owner?.id ?: ''),
                        options: ownerOptions
                ])}

            </fieldset>
            <fieldset>
                <legend>${ui.message("imbemr.insurance")}</legend>

                ${ ui.includeFragment("uicommons", "field/dropDown", [
                        label: ui.message("imbemr.insurance.name"),
                        emptyOptionLabel: "",
                        formFieldName: "insuranceId",
                        initialValue: (policy.insuranceId ?: ''),
                        options: insuranceOptions
                ])}

                ${ ui.includeFragment("uicommons", "field/text", [
                        label: ui.message("imbemr.insurance.insuranceCardNo"),
                        formFieldName: "insuranceCardNo",
                        initialValue: (policy.insuranceCardNo ?: ''),
                        size: 30,
                        otherAttributes: ["autocomplete": "off"]
                ])}

                ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                        id: "policy-coverarge-start-date-picker",
                        label: ui.message("imbemr.insurance.coverageStartDate"),
                        formFieldName: "coverageStartDate",
                        defaultDate: policy.coverageStartDate,
                        useTime: false
                ])}

                ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                        id: "policy-coverarge-expiration-date-picker",
                        label: ui.message("imbemr.insurance.expirationDate"),
                        formFieldName: "expirationDate",
                        defaultDate: policy.expirationDate,
                        useTime: false
                ])}
                <% if (policy.expirationDate != null && policy.expirationDate < new Date()) { %>
                    <b style="color:red;">${ui.message("imbemr.expired")}</b>
                <% } %>

                ${ ui.includeFragment("uicommons", "field/dropDown", [
                        label: ui.message("imbemr.insurance.thirdParty"),
                        emptyOptionLabel: "",
                        formFieldName: "thirdPartyId",
                        initialValue: (policy.thirdPartyId ?: ''),
                        options: thirdPartyOptions
                ])}
            </fieldset>
        </td>
        <td>
            <fieldset>
                <legend>${ui.message("imbemr.insurance.ownershipInfo")}</legend>

                ${ ui.includeFragment("uicommons", "field/text", [
                        label: ui.message("imbemr.insurance.beneficiary.company"),
                        formFieldName: "company",
                        initialValue: (policy.company ?: ''),
                        size: 30
                ])}

                ${ ui.includeFragment("uicommons", "field/text", [
                        label: ui.message("imbemr.insurance.beneficiary.ownerName"),
                        formFieldName: "ownerName",
                        initialValue: (policy.ownerName ?: ''),
                        size: 30
                ])}

                ${ ui.includeFragment("uicommons", "field/text", [
                        label: ui.message("imbemr.insurance.beneficiary.ownerCode"),
                        formFieldName: "ownerCode",
                        initialValue: (policy.ownerCode ?: ''),
                        size: 30
                ])}

                ${ ui.includeFragment("uicommons", "field/dropDown", [
                        label: ui.message("imbemr.insurance.beneficiary.level"),
                        emptyOptionLabel: "",
                        formFieldName: "level",
                        initialValue: (policy.level ?: ''),
                        options: levelOptions
                ])}

            </fieldset>

        </td>
    </tr></table>

    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="document.location.href = '${returnUrl}';" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>

</form>