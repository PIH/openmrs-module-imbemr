package org.openmrs.module.imbemr;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.AppTemplate;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.factory.AppFrameworkFactory;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImbEmrAppLoaderFactory implements AppFrameworkFactory {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private ObjectMapper objectMapper = new ObjectMapper();

    public ImbEmrAppLoaderFactory() {
    	// Tell the parser to all // and /* style comments.
    	objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    private List<File> getAppConfigFilesBySuffix(String suffix) {
        List<File> files = new ArrayList<>();
        File configDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory("configuration");
        File appFrameworkDir = new File(configDir, "appframework");
        File appDir = new File(appFrameworkDir, "apps");
        if (appDir.exists() && appDir.isDirectory()) {
            for (File f : appDir.listFiles()) {
                if (f.getName().endsWith(suffix)) {
                    files.add(f);
                }
            }
        }
        return files;
    }
    
    @Override
    public List<AppTemplate> getAppTemplates() {
        List<AppTemplate> templates = new ArrayList<>();
        for (File f : getAppConfigFilesBySuffix("AppTemplates.json")) {
            try {
                List<AppTemplate> l = objectMapper.readValue(f, new TypeReference<List<AppTemplate>>() {});
                templates.addAll(l);
            }
            catch (Exception e) {
                logger.error("Error reading AppTemplates configuration file: {}", f.getName(), e);
            }
        }
        return templates;
    }

    @Override
    public List<AppDescriptor> getAppDescriptors() {
        List<AppDescriptor> descriptors = new ArrayList<>();
        for (File f : getAppConfigFilesBySuffix("app.json")) {
            try {
                List<AppDescriptor> l = objectMapper.readValue(f, new TypeReference<List<AppDescriptor>>() {});
                descriptors.addAll(l);
            }
            catch (Exception e) {
                logger.error("Error reading AppDescriptors configuration file: {}", f.getName(), e);
            }
        }
        return descriptors;
    }


    @Override
    public List<Extension> getExtensions() {
        List<Extension> extensions = new ArrayList<>();
        for (File f : getAppConfigFilesBySuffix("extension.json")) {
            try {
                List<Extension> l = objectMapper.readValue(f, new TypeReference<List<Extension>>() {});
                extensions.addAll(l);
            }
            catch (Exception e) {
                logger.error("Error reading Extension configuration file: {}", f.getName(), e);
            }
        }
        return extensions;
    }
}
