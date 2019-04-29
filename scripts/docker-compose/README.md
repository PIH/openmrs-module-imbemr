# Deploying the IMB Distribution using Docker Compose

This project contains the relevant files to run the IMB distribution using Docker.  It utilizes docker-compose, which enables connecting several independent
dockerized services together (in this case MySQL and Tomcat), and maintaining configuration for these within a docker-compose.yml file.

If you haven't already done do, you will need to [Install Docker](https://docs.docker.com/) and [Install Docker Compose](https://docs.docker.com/compose/)

### Step 1:  Download the necessary deployment artifacts

This Docker container mounts in several host files/directories to provide the webapps, modules, and initial database script.
You will need to download these as appropriate (from production servers, or from maven) and put into directories on your
computer.  You will link to these directories in the step below.

### Step 2:  Adjust the included configuration files as needed

There is a default ".env" file included with the following defaults

MYSQL_ROOT_PASSWORD=root
MYSQL_PASSWORD=openmrs
MYSQL_PORT=3308
OPENMRS_SERVER_PORT=8080
MYSQL_INITIAL_DB_PATH=~/environments/openmrs/rwink.sql
OPENMRS_WEBAPPS_PATH=~/environments/rwink/webapps
OPENMRS_MODULES_PATH=~/environments/rwink/modules

These parameters should be modified to meet your environment.

You may also override the following defaults that are included:

OPENMRS_MEMORY_OPTS="-Xmx2048m -Xms1024m -XX:PermSize=256m -XX:MaxPermSize=512m -XX:NewSize=128m"
OPENMRS_OTHER_OPTS="-server -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Djava.awt.headlesslib=true"

### Step 3:  Build images and run the containers for the first time

From the base directory (the one that contains your docker-compose.yml file) run:

`docker-compose up --build`

This should successfully start and run MySQL and Tomcat, running the OpenMRS web application

### Step 4:  Start/Stop this

To stop, Ctrl-C out of it.  You could also bring down by typing:

`docker-compose down`

You can start it back up again with:

`docker-compose up`


### Working with the running containers

Some useful commands that are helpful to work with these containers while they are running:

**Open up a bash shell for working within a container**: 

`docker exec -it <container-name> /bin/bash`

For example, let's say you want to use the mysql client within the mysql container to look around the database a bit:

1. `docker ps` - will show you the names of which containers are running.
2. `docker exec -it container_name /bin/bash` - this will put you into a bash shell __inside__ the container.  It will look something like `root@d7e7bd809849:/#`
3. `mysql -u openmrs -p openmrs` - Now that you are inside the container, you can run the normal slew of mysql commands as if it is local

**Tail the log file of a particular container**

`docker logs -f <container-name>` (see https://docs.docker.com/engine/reference/commandline/logs/)

For example, let's say you want to tail the Tomcat logs":

1. `docker ps` - will show you the names of which containers are running.
2. `docker logs -f container_name` - will tail the log file.  Hit Ctrl-C to exit.
