<%
    def patient = config.patient
%>
<div class="info-section">
    <div class="info-header">
        <i class="icon-calendar"></i>
        <h3>${ ui.message(config.label ? config.label : "imbemr.clinicianfacing.recentEncounters").toUpperCase() }</h3>
        <a href="${ui.pageLink("imbemr", "patient/encounterList", ["patientId": e.patient.uuid])}" class="right">
            <i class="icon-share-alt edit-action" title="${ ui.message("imbemr.clinicianfacing.recentEncounters") }"></i>
        </a>
    </div>
    <div class="info-body">
        <% if (recentVisitsWithLinks.isEmpty()) { %>
            ${ui.message("coreapps.none")}
        <% } %>
        <ul>
            <% encounters.each { encounter ->
                var url = ui.pageLink("htmlformentryui", "htmlform/viewEncounterWithHtmlForm", [
                        "patientId"     : encounter.patient.uuid,
                        "encounterId"   : encounter.uuid,
                        "returnProvider": "coreapps",
                        "returnPage"    : "clinicianfacing/patient"])
            }
            %>
                <li class="clear">
                    <a id="${encounter.id}" href="${url}" class="encounter-link">
                        <script type="text/javascript">
                            jq("#${encounter.id}.encounter-link").click(function () {
                                window.location.href = "${url}";
                            });
                        </script>
                        ${ ui.formatDatePretty(encounter.encounterDatetime) }
                    </a>
                    <span id="encountertype-tag-${encounter.id}" class="tag" >
                        ${ ui.format(encounter.encounterType)}
                    </span>
                </li>
            <% } %>
        </ul>
    </div>
</div>
