<%
    ui.decorateWith("appui", "standardEmrPage")
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<h3>Migrate Visit Locations</h3>

<% if (visitsToMigrate.size() == 0) { %>

    This patient has no visits that need to be migrated

<% } else { %>

    <table>
        <thead>
            <tr>
                <th>Visit Date</th>
                <th>Encounters</th>
                <th>Current Location</th>
                <th>Correct Location</th>
            </tr>
        </thead>
        <tbody>

            <% visitsToMigrate.keySet().each { visit -> %>
                <tr>
                    <td>
                        ${ ui.format(visit.startDatetime) }
                    </td>
                    <td>
                        <% visit.encounters.eachWithIndex{ encounter, index -> %>
                            ${index == 0 ? "" : "<br/>"}
                            ${ui.format(encounter.encounterDatetime)}:
                            ${ui.format(encounter.encounterType)} at ${ui.format(encounter.location)}
                        <% } %>
                    </td>
                    <td>
                        ${ ui.format(visit.location) }
                    </td>
                    <td>
                        ${ ui.format(visitsToMigrate.get(visit)) }
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
    <div>
        <form method="post">
            <input type="hidden" name="patientId" value="${patient.patient.patientId}" />
            <input type="submit" class="submit" value="Migrate"/>
        </form>
    </div>
<% } %>



