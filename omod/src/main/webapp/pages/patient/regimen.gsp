<%
    ui.decorateWith("appui", "standardEmrPage")
%>
<style>
    body {
        max-width: 1000px;
    }
    .frame-content {
        width: 100%;
        height: 70vh;
        overflow: hidden;
        border: none;
    }
</style>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }

<h3>Regimens</h3>

<iframe class="frame-content" scrolling="no" src="/${ contextPath }/module/imbemr/patientRegimen.htm?patientId=${ patient.patientId }"></iframe>