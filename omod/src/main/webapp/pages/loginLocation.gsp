<%
    ui.decorateWith("appui", "standardEmrPage")
    def visitAndLoginLocations = locationTagUtil.getValidVisitAndLoginLocations()
    def visitLocations = visitAndLoginLocations.keySet()
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("imbemr.login.chooseLocation.title") }" }
    ];
</script>

<style>
    .login-location-select {
        display: none
    }
    #visit-location-section {
        padding-bottom: 20px;
    }
</style>

<script type="text/javascript">
    jq(document).ready(function() {

        <% if (visitLocations.size() == 1) { %>
            jq('#login-location-section-${visitLocations.get(0).id}').show();
        <% } %>

        jq(".login-location-section").hide();
        jq(".login-location-select").hide();

        jq(".visit-location-select .location-list-item").click(function() {
            let id = jq(this).attr('value');
            console.log('Visit Location selected ' + id);

            jq(".login-location-select").hide();
            jq(".login-location-section").hide();

            let loginLocationElements = jq("#login-location-select-"+id).find('.location-list-item');
            if (loginLocationElements.length > 0) {
                jq(loginLocationElements[0]).click();
            }
            else {
                jq("#login-location-select-"+id).show();
                jq("#login-location-section-"+id).show();
            }
        });

        jq(".login-location-select .location-list-item").click(function() {
            let id = jq(this).attr('value');
            console.log('Login Location selected ' + id);
            jq("#session-location-input").val(id);
            <% if (!locationTagUtil.isLocationSetupRequired()) { %>
                jq("#login-location-form").submit();
            <% } %>
        });
    });
</script>

<% if (locationTagUtil.isLocationSetupRequired()) { %>
    <style>
    .setup-location-tag-link {
        color: blue;
        text-decoration: underline;
    }
    </style>
    <div class="note-container">
        <div class="note warning" style="width: 100%;">
            <div class="text">
                <i class="fas fa-fw fa-exclamation-circle" style="vertical-align: middle;"></i>
                <% if (sessionContext.currentUser.hasPrivilege("App: coreapps.systemAdministration")) { %>
                    <a class="setup-location-tag-link" href="${ ui.pageLink("imbemr", "admin/configureLoginLocations") }">
                        ${ ui.message("imbemr.login.warning.invalidLoginLocations") }
                    </a>
                <% } else { %>
                    ${ ui.message("imbemr.login.warning.invalidLoginLocations") }
                <% } %>
            </div>
        </div>
    </div>
<% } %>

<form id="login-location-form" method="post">
    <!-- only show visit location selector if there are multiple locations to choose from -->
    <% if (visitLocations.size() > 1) { %>
        <div class="clear" id="visit-location-section">
            <label>
                ${ ui.message("imbemr.login.chooseVisitLocation.title") }:
            </label>
            <ul class="select visit-location-select">
                <% visitLocations.each { visitLocation -> %>
                    <li class="location-list-item" value="${visitLocation.id}">${ui.format(visitLocation)}</li>
                <% } %>
            </ul>
        </div>
    <% } %>

    <div id="login-location-section">
        <% visitLocations.each { visitLocation ->
            def loginLocations = visitAndLoginLocations.get(visitLocation) %>
            <% if (loginLocations.size() > 0) { %>
                <div class="clear login-location-section" id="login-location-section-${visitLocation.id}">
                    <label>
                        ${ ui.message("imbemr.login.chooseLoginLocation.title") }:
                    </label>
                    <ul id="login-location-select-${visitLocation.id}" class="select login-location-select">
                        <% loginLocations.each { %>
                            <li class="location-list-item" value="${visitLocation.id}">${ui.format(visitLocation)}</li>
                        <% } %>
                    </ul>
                </div>
            <% } %>
        <% } %>
    </div>

    <input id="session-location-input" type="hidden" name="sessionLocation" />
</form>