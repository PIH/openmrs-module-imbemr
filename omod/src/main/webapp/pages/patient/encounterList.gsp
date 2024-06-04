<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "datatables/jquery.dataTables.min.js")
%>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient ]) }

<script type="text/javascript">

    jq(document).ready(function() {
       jq("#return-button").click(function(event) {
           document.location.href = '${ui.pageLink("coreapps", "clinicianfacing/patient", ["patientId": patient.id])}';
       });

      jq(".encounter-row").click(function() {
        if (jq(this).data("href")) {
          document.location.href = jq(this).data("href");
        }
      });

        <% if (encounters.size() > 0) { %>

            // Get all the unique encounter types, ordered alphabetically, that are present in the list, and add to select list filter
            let encTypes = [];
            jq(".encounterTypeColumn").each(function() {
                encTypes.push(this.innerHTML.trim());
            });
            encTypes.sort();
            let lastType = null;
            encTypes.forEach(function(typeName) {
                if (lastType === null || lastType !== typeName) {
                    jq("#encounter-type-filter").append('<option value="' + typeName + '">' + typeName + '</option>');
                }
                lastType = typeName;
            });

            // Create a datatable of the encounter data
            let encTable = jq("#encounter-list-table").dataTable(
                {
                    bFilter: true,
                    bJQueryUI: true,
                    bLengthChange: false,
                    iDisplayLength: 5,
                    sPaginationType: 'full_numbers',
                    bSort: false,
                    sDom: 'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>',
                    oLanguage:  {
                        oPaginate: {
                            sFirst: "${ ui.message("uicommons.dataTable.first") }",
                            sLast: "${ ui.message("uicommons.dataTable.last") }",
                            sNext:  "${ ui.message("uicommons.dataTable.next") }",
                            sPrevious:  "${ ui.message("uicommons.dataTable.previous") }"
                        },

                        sInfo:  "${ ui.message("uicommons.dataTable.info") }",
                        sSearch: "${ ui.message("uicommons.dataTable.search") }",
                        sZeroRecords: "${ ui.message("uicommons.dataTable.zeroRecords") }",
                        sEmptyTable: "${ ui.message("uicommons.dataTable.emptyTable") }",
                        sInfoFiltered:  "${ ui.message("uicommons.dataTable.infoFiltered") }",
                        sInfoEmpty:  "${ ui.message("uicommons.dataTable.infoEmpty") }",
                        sLengthMenu:  "${ ui.message("uicommons.dataTable.lengthMenu") }",
                        sLoadingRecords:  "${ ui.message("uicommons.dataTable.loadingRecords") }",
                        sProcessing:  "${ ui.message("uicommons.dataTable.processing") }",

                        oAria: {
                            sSortAscending:  "${ ui.message("uicommons.dataTable.sortAscending") }",
                            sSortDescending:  "${ ui.message("uicommons.dataTable.sortDescending") }"
                        }
                    }
                }
            );
        <% } %>

        // Filter the datatable based on the encounter type list

        jq("#encounter-type-filter").change(function() {
           let typeName = jq(this).val();
           if (typeName !== '') {
               typeName = typeName.replace("\\(", "\\\\(");
               typeName = typeName.replace("\\)", "\\\\)");
               typeName = '^(\\s*)' + typeName + '(\\s*)\$'; // Regex to ensure exact match (eg. "Admission" should not return "COVID-19 Admission")
           }
           // https://legacy.datatables.net/api
           // First parameter is regex on encounter type, to ensure only exact match is filtered
           // Second parameter is the 0-indexed column number to filter on with encounter type
           // Third parameter is true to indicate that a regex-based search should be used

           encTable.fnFilter(typeName, 1, true);
        });

    });
</script>

<style>
    .date-column {
        width: 125px;
    }
    .encounter-link {
        cursor:pointer;
        color:blue;
        text-decoration:underline;
    }
    .pointer {
        cursor:pointer;
    }
    #encounter-filters {
        padding: 10px;
    }
    #encounter-list-table_filter {
        display:none;
    }
</style>
<h3>${ ui.message("imbemr.encounterList") }</h3>

<div id="encounter-filters">
    <span id="encounter-type-filter-label">${ ui.message("imbemr.encounterList.encounterType") }:</span>
    <select id="encounter-type-filter">
        <option value="">${ ui.message("imbemr.all") }</option>
    </select>
</div>

<table id="encounter-list-table">
    <thead>
        <tr>
            <th>${ ui.message("imbemr.encounterList.encounterDatetime") }</th>
            <th>${ ui.message("imbemr.encounterList.encounterType") }</th>
            <th>${ ui.message("imbemr.encounterList.provider") }</th>
            <th>${ ui.message("imbemr.encounterList.location") }</th>
            <th>${ ui.message("imbemr.encounterList.enteredDatetime") }</th>
        </tr>
    </thead>
    <tbody>
    <% if (encounters.size() == 0) { %>
        <tr>
            <td colspan="5">${ ui.message("emr.none") }</td>
        </tr>
    <% } %>
    <% encounters.each { e ->

        def pageLink

        if (e.visit) {
            pageLink = ui.pageLink("coreapps", "patientdashboard/patientDashboard", [
                "patientId": e.patient.uuid,
                "visitId": e.visit.uuid,
            ])
        }
        // the assumption here is that if there's an associated form, it's an HTML Form... once we start adopting O3/Ampath forms this may no longer be valid
        else if (e.form) {
            pageLink = ui.pageLink("htmlformentryui", "htmlform/viewEncounterWithHtmlForm", [
                    "patientId": e.patient.uuid,
                    "encounterId": e.uuid,
                    "returnProvider": "imbemr",
                    "returnPage": "patient/encounterList"])
        }

        %>
        <tr id="encounter-${ e.encounterId }" class="encounter-row${pageLink ? ' pointer' :''}" data-href="${pageLink}">
            <td class="date-column">
                ${ ui.format(e.encounterDatetime) }
            </td>

            <td class="encounterTypeColumn${pageLink ? ' encounter-link' :''}">
                ${ ui.format(e.encounterType) }
            </td>
            <td>
                <% e.encounterProviders.eachWithIndex { ep, index -> %>
                    ${ ui.format(ep.provider) }${ e.encounterProviders.size() - index > 1 ? "<br/>" : ""}
                <% } %>
            </td>
            <td>
                ${ ui.format(e.location) }
            </td>
            <td class="date-column">
                ${ ui.format(e.dateCreated) }
            </td>
        </tr>
    <% } %>
    </tbody>
</table>

<div>
    <input id="return-button" type="button" class="cancel" value="${ ui.message("imbemr.encounterList.return") }"/>
</div>


