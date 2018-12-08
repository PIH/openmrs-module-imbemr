openmrs-module-imbemr
===================================

Using the OpenMRS SDK to create a Rwanda development enviroment
---------------------------------------------------------------

1. Install the OpenMRS SDK as described here: https://wiki.openmrs.org/display/docs/OpenMRS+SDK#OpenMRSSDK-Installation
2. Clone this project
3. Run "mvn clean install" on this project
4. Make sure that the "properties" variables in the main IMB pom are all set the proper versions of modules you want to run
5. Run "mvn openmrs-sdk:setup" (from any directory) --
    **Note**: if the build fails at any time during this process because it is saying it can't find a module in the Maven repo, you may need
    to check out that module and install it locally ("mvn clean install")
    1. Pick the name of the server (this will determine the subdirectory off ~/openmrs/ where it created the information for this server--in my case, I used 'rwanda'
    2. For "Distribution" or "Platform" chose "Distribution"
    3. For distribution version chose "Other...."
    4. For the distribution, use this module "org.openmrs.module:imbemr:1.0-SNAPSHOT" (or whatever the current version of this module is)
    5. Choose the port to run tomcat on (usually 8080)
    5. Choose port to debug on (usually 1044)
    6. Chose how you want to use MySQL... via a local install of MySQL or docker
    7. For the DB name, chose the existing Rwanda database you want to use: make sure to select the option to NOT overwrite the existing database
    8. Enter mysql user/password for a user that has rights to create databases, etc
    9. Chose the JAVA HOME you want to use (should be Java 7)

6. Run "mvn openmrs-sdk:run"


Updating your Rwanda development environment when module versions change
------------------------------------------------------------------------

1. *From the base directory of this module*, run: "mvn openmrs-sdk:deploy -Ddistro=api/src/main/resources/openmrs-distro.properties"
2. Then just start the server via "mvn openmrs-sdk:run"


Other OpenMRS SDK tips and tricks
---------------------------------

You can use the OpenMRS SDK to "watch" modules (so that an openmrs-sdk:run automatically deploys any changes you make to those modules).  


For more details, read more about the SDK here:

https://wiki.openmrs.org/display/docs/OpenMRS+SDK#OpenMRSSDK-Installation
