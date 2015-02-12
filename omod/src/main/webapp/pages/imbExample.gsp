<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.min.js")
    ui.includeJavascript("uicommons", "services/encounterService.js")


    ui.includeJavascript("imbemr", "imbExample.js")
%>

<div id="imb-example-app" ng-controller="ImbExampleCtrl">

    <div>
        Search for a patient:
        <input type="text" size="40" ng-model="patientSearch" ng-keydown="searchForPatients(patientSearch)"/>
        <button ng-click="searchForPatients(patientSearch)">Do Search</button>
    </div>

    <div ng-show="patientsFound">
        Found {{ patientsFound.length }} patient(s)

        <table>
            <tbody>
            <tr ng-repeat="patient in patientsFound">
                <td>{{ patient.identifiers[0].display }}</td>
                <td>{{ patient.person.preferredName.display }}</td>
                <td>
                    <a ng-click="selectPatient(patient)">Select</a>
                </td>
            </tr>
            </tbody>

        </table>
    </div>

    <div ng-show="selectedPatient">

        <h3>Enter weight and height for {{ selectedPatient.person.display }}</h3>

        <form ng-submit="saveWeightAndHeight()">
            <p>
                <label>Weight(Kg)</label>
                <input ng-model="weight" ng-change="calculateBmi()"/>
            </p>

            <p>
                <label>Height(cm)</label>
                <input ng-model="height" ng-change="calculateBmi()"/>
            </p>

            <p ng-show="weight && height">
                <label>Calculated the BMI</label>
                {{ calculateBmi() }}
            </p>

            <div class="error" ng-show="errorMessage">{{ errorMessage }}</div>

            <input type="submit" />
        </form>


    </div>

</div>

<script type="text/javascript">
    angular.bootstrap('#imb-example-app', [ 'imbExample' ]);
</script>