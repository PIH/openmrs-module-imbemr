<% if (clientRegistryEnabled) { %>

    <style>
        #search-client-registry-section {
            padding-top: 20px;
        }
        #client-registry-match-section {
            display: none;
            padding: 20px;
            background-color: blue;
            color: white;
            position: relative;
            z-index: 10000;
        }
        #search-client-registry-loading-spinner {
            display: none;
        }
    </style>

    <p id="search-client-registry-section" class="left">
        <input id="client-registry-search-button" type="button" value="${ui.message("imbemr.clientRegistry.search")}" />
        <i id="search-client-registry-loading-spinner" class="icon-spinner icon-spin"></i>
    </p>

    <script type="text/javascript">
        jq(function() {

            jq("#client-registry-search-button").click(function() {
                jq("#client-registry-search-button").prop('disabled', true);
                jq("#search-client-registry-loading-spinner").show();
                jq.ajax({
                    url: "${ ui.actionLink("imbemr", "field/searchClientRegistry", "findByIdentifier") }",
                    dataType: "json",
                    data: {
                        'identifier': jq("#national-ids-questions input[name='upid']").val(),
                        'identifierTypeUuid': '01edaedd-956a-11ef-93fa-0242ac120002'
                    },
                    success: function( data ) {
                        jq('#search-client-registry-loading-spinner').hide();
                        jq("#client-registry-search-button").removeProp('disabled');
                        for (const [key, value] of Object.entries(data)) {
                            jq("#registration [name='" + key + "']").val(value);
                        }
                        console.log("Success!");
                        console.log(data);
                    },
                    error: function( data ) {
                        jq('#search-client-registry-loading-spinner').hide();
                        jq("#client-registry-search-button").removeProp('disabled');
                        console.log("Error!");
                        console.log(data);
                    }
                });
            });

            jq("#client-registry-close-button").click(function() {
                jq("#client-registry-match-section").hide();
            });
        });
    </script>

    <div id="client-registry-match-section" class="dialog">
        <div class="dialog-header">
            <h3>${ ui.message("imbemr.clientRegistry.matchFound") }</h3>
        </div>
        <div class="dialog-content">
            <p class="dialog-instructions">
                Patient info here
            </p>
            <input id="client-registry-import-button" type="button" value="${ ui.message("imbemr.clientRegistry.import") }"/>
            <input id="client-registry-close-button" type="button" value="${ ui.message("imbemr.clientRegistry.close") }"/>
        </div>
    </div>

<% } %>