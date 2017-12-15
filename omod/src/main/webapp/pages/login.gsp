<%
    ui.includeFragment("appui", "standardEmrIncludes")
    ui.includeCss("imbemr", "login.css")
%>

<!DOCTYPE html>
<html>
<head>
    <title>${ ui.message("imbemr.login.title") }</title>
    <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
    <link rel="icon" type="image/png\" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
    ${ ui.resourceLinks() }
</head>
<body>
<script type="text/javascript">
    var OPENMRS_CONTEXT_PATH = '${ ui.contextPath() }';
</script>


${ ui.includeFragment("imbemr", "infoAndErrorMessages") }

<script type="text/javascript">
    jQuery(document).ready(function(){
        jQuery('#username').focus();
    });

    updateSelectedOption = function() {
        jQuery('#sessionLocation li').removeClass('selected');
        var hiddenLocationId = jQuery('#sessionLocationInput').val();
        if ( hiddenLocationId && (parseInt(hiddenLocationId) >= 0) ) {
            jQuery('#sessionLocation li[value|=' + hiddenLocationId + ']').addClass('selected');
        }


    };

    jQuery(function() {
        updateSelectedOption();

        jQuery('#sessionLocation li').click( function() {
            jQuery('#sessionLocationInput').val(jQuery(this).attr("value"));
            updateSelectedOption();
        });

        var cannotLoginController = emr.setupConfirmationDialog({
            selector: '#cannot-login-popup',
            actions: {
                confirm: function() {
                    cannotLoginController.close();
                }
            }
        });
        jQuery('a#cant-login').click(function() {
            cannotLoginController.show();
        })
    });
</script>

<header>
    <div class="logo">
        <a href="${ui.pageLink("imbemr", "login")}">
            <img src="${ui.resourceLink("imbemr", "images/IMBLogo.png")}"/>
        </a>
    </div>
</header>

<div id="body-wrapper">
    <div id="content">
        <form id="login-form" method="post" autocomplete="off">
            <fieldset>

                <legend>
                    <i class="icon-lock small"></i>
                    ${ ui.message("imbemr.login.loginHeading") }
                </legend>

                <p class="left">
                    <label for="username">
                        ${ ui.message("imbemr.login.username") }:
                    </label>
                    <input id="username" type="text" name="username" placeholder="${ ui.message("imbemr.login.username.placeholder") }"/>
                </p>

                <p class="left">
                    <label for="password">
                        ${ ui.message("imbemr.login.password") }:
                    </label>
                    <input id="password" type="password" name="password" placeholder="${ ui.message("imbemr.login.password.placeholder") }"/>
                </p>

                <p class="clear">
                    <label for="sessionLocation">
                        ${ ui.message("imbemr.login.sessionLocation") }:
                    </label>
                <ul id="sessionLocation" class="select">
                    <li id="No Location" value="0">No Location</li>
                    <% locations.sort { ui.format(it) }.each { %>
                    <li id="${it.name}" value="${it.id}">${ui.format(it)}</li>
                    <% } %>
                </ul>
            </p>

                <input type="hidden" id="sessionLocationInput" name="sessionLocation"
                    <% if (lastSessionLocation != null) { %> value="${lastSessionLocation.id}" <% } %> />

                <p></p>
                <p>
                    <input id="login-button" class="confirm" type="submit" value="${ ui.message("imbemr.login.button") }"/>
                </p>
                <p>
                    <a id="cant-login" href="javascript:void(0)">
                        <i class="icon-question-sign small"></i>
                        ${ ui.message("imbemr.login.cannotLogin") }
                    </a>
                </p>

            </fieldset>

            <input type="hidden" name="redirectUrl" value="${redirectUrl}" />

        </form>

    </div>
</div>

<div id="cannot-login-popup" class="dialog" style="display: none">
    <div class="dialog-header">
        <i class="icon-info-sign"></i>
        <h3>${ ui.message("imbemr.login.cannotLogin") }</h3>
    </div>
    <div class="dialog-content">
        <p class="dialog-instructions">${ ui.message("imbemr.login.cannotLoginInstructions") }</p>

        <button class="confirm">${ ui.message("imbemr.okay") }</button>
    </div>
</div>

</body>
</html>