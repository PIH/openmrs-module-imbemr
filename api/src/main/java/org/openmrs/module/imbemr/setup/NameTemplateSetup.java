package org.openmrs.module.imbemr.setup;

import org.openmrs.api.context.Context;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameTemplateSetup {

    public static void setup() {
        for (NameSupport nameSupport : Context.getRegisteredComponents(NameSupport.class)) {
            configureNameTemplate(nameSupport);
        }
        configureNameTemplate(NameSupport.getInstance());
    }

    public static void configureNameTemplate(NameSupport nameSupport) {
        NameTemplate nameTemplate = new NameTemplate();
        nameTemplate.setCodeName("short");  // we are redefining the short name template for use in our context

        Map<String, String> nameMappings = new HashMap<>();
        nameMappings.put("givenName", "imbemr.person.givenName");
        nameMappings.put("familyName", "imbemr.person.familyName");
        nameTemplate.setNameMappings(nameMappings);

        Map<String, String> sizeMappings = new HashMap<>();
        sizeMappings.put("givenName", "50");
        sizeMappings.put("familyName", "50");
        nameTemplate.setSizeMappings(sizeMappings);

        List<String> lineByLineFormat = new ArrayList<>();
        lineByLineFormat.add("familyName");
        lineByLineFormat.add("givenName");

        nameTemplate.setLineByLineFormat(lineByLineFormat);

        List<NameTemplate> templates = new ArrayList<>();
        templates.add(nameTemplate);

        // we blow away the other templates here
        nameSupport.setLayoutTemplates(templates);
        nameSupport.setDefaultLayoutFormat("short");
    }

}
