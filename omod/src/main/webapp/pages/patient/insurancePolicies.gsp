<%
    ui.decorateWith("appui", "standardEmrPage")
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">
    jq(document).ready(function() {
       jq("#return-button").click(function(event) {
           document.location.href = '${ui.pageLink("registrationapp", "registrationSummary", ["patientId": patient.id, "appId": "imbemr.registerPatient"])}';
       });
        jq("#add-new-button").click(function(event) {
            document.location.href = '${ui.pageLink("imbemr", "patient/insurancePolicy", ["patientId": patient.id])}';
        });
    });
</script>

<h3>${ ui.message("imbemr.insurancePolicies") }</h3>

<table id="insurance-policy-table">
    <thead>
        <tr>
            <th>${ ui.message("imbemr.insurance.name") }</th>
            <th>${ ui.message("imbemr.insurance.insuranceCardNo") }</th>
            <th>${ ui.message("imbemr.insurance.coverageStartDate") }</th>
            <th>${ ui.message("imbemr.insurance.expirationDate") }</th>
            <th>${ ui.message("imbemr.insurance.thirdParty") }</th>
        </tr>
    </thead>
    <tbody>
    <% if (insurancePolicies.size() == 0) { %>
        <tr>
            <td colspan="5">${ ui.message("emr.none") }</td>
        </tr>
    <% } %>
    <% insurancePolicies.each { policy -> %>
        <tr>
            <td>
                <a href="${ui.pageLink("imbemr", "patient/insurancePolicy", ["patientId": patient.patient.patientId, "policyId": policy.insurancePolicyId])}">
                    ${ ui.format(policy.insurance?.name) }
                </a>
            </td>
            <td>
                <a href="${ui.pageLink("imbemr", "patient/insurancePolicy", ["patientId": patient.patient.patientId, "policyId": policy.insurancePolicyId])}">
                    ${ ui.format(policy.insuranceCardNo) }
                </a>
            </td>
            <td>
                ${ ui.format(policy.coverageStartDate) }
            </td>
            <td>
                ${ ui.format(policy.expirationDate) }
            </td>
            <td>
                ${ ui.format(policy.thirdParty?.name) }
            </td>
        </tr>
    <% } %>
    </tbody>
</table>
<br/>
<div>
    <input id="return-button" type="button" value="${ ui.message("imbemr.registration.summary") }"/>
    <input id="add-new-button" type="button" value="${ ui.message("imbemr.insurancePolicies.add") }"/>
</div>


