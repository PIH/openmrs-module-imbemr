<%
    ui.decorateWith("appui", "standardEmrPage")
%>
<style>
    body {
        max-width: unset;
    }
    .frame-content {
        width: 100%;
        height: 2000px;
        overflow: hidden;
        border: none;
    }
</style>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

<h3>MOH Billing</h3>

<iframe class="frame-content" scrolling="no" src="/${ contextPath }/module/imbemr/manualBilling.htm?patientId=${ patient.patientId }"></iframe>
