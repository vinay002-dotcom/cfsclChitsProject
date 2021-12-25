#  -------------------------------------------- Chetana Chits -------------------------------------------- 
This repo contains backend services and db connectivity code. This is a J2EE project and is built on top of Apache Fineract: A Platform for Microfinance  

Fineract is a mature platform with open APIs that provides a reliable, robust, and affordable core banking solution for financial institutions offering services to the world’s 2 billion underbanked and unbanked.

[Have a look at the FAQ on our Wiki at apache.org](https://cwiki.apache.org/confluence/display/FINERACT/FAQ) 

Requirements
============
* Java >= 11 (OpenJDK JVM is tested by our CI on Travis)
* MySQL 5.7

You can run the required version of the database server in a container, instead of having to install it, like this:

    docker run --name mysql-5.7 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=mysql -d mysql:5.7

and stop and destroy it like this:

    docker rm -f mysql-5.7

Beware that this database container database keeps its state inside the container and not on the host filesystem.  It is lost when you destroy (rm) this container.  This is typically fine for development.  See [Caveats: Where to Store Data on the database container documentation](https://hub.docker.com/_/mysql) re. how to make it persistent instead of ephemeral.

Tomcat v9 is only required if you wish to deploy the Fineract WAR to a separate external servlet container.  Note that you do not require to install Tomcat to develop Fineract, or to run it in production if you use the self-contained JAR, which transparently embeds a servlet container using Spring Boot.  (Until FINERACT-730, Tomcat 7/8 were also supported, but now Tomcat 9 is required.)


Instructions how to run for local development
============

Run the following commands:
1. `./gradlew createDB -PdbName=fineract_tenants`
1. `./gradlew createDB -PdbName=fineract_default`
1. `./gradlew bootRun`


Instructions to build the JAR file
============
1. Clone the repository or download and extract the archive file to your local directory.
2. Run `./gradlew clean bootJar` to build a modern cloud native fully self contained JAR file which will be created at `build/libs` directory.
3. Start it using `java -jar build/libs/fineract-provider.jar` (does not require external Tomcat)

The tenants database connection details are configured [via environment variables (as with Docker container)](#instructions-to-run-using-docker-and-docker-compose), e.g. like this:

    export fineract_tenants_pwd=verysecret
    ...
    java -jar fineract-provider.jar


Instructions to build a WAR file
============
1. Clone the repository or download and extract the archive file to your local directory.
2. Run `./gradlew clean war` to build a traditional WAR file which will be created at `build/libs` directory.
3. Deploy this WAR to your Tomcat v9 Servlet Container.

We recommend using the JAR instead of the WAR file deployment, because it's much easier.

Note that with the 1.4 release the tenants database pool configuration changed from Tomcat DBCP in XML to an embedded Hikari, configured by environment variables, see above.


Instructions to execute Integration Tests
============
> Note that if this is the first time to access MySQL DB, then you may need to reset your password.

Run the following commands, very similarly to how [.travis.yml](.travis.yml) does:
1. `./gradlew createDB -PdbName=fineract_tenants`
1. `./gradlew createDB -PdbName=fineract_default`
1. `./gradlew clean test`


Instructions to run and debug in Eclipse IDE
============

It is possible to run Fineract in Eclipse IDE and also to debug Fineract using Eclipse's debugging facilities.
To do this, you need to create the Eclipse project files and import the project into an Eclipse workspace:

1. Import the fineract-provider project into your Eclipse workspace (File->Import->Gradle->Existing Gradle Project into Workspace, choose root directory fineract/fineract-provider)
2. Do a clean build of the project in Eclipse (Project->Clean...)
3. Run / debug Fineract by right clicking on org.apache.fineract.ServerApplication class and choosing Run As / Debug As -> Java Application. All normal Eclipse debugging features (breakpoints, watchpoints etc) should work as expected.

You can also use Eclipse Junit support to run tests in Eclipse (Run As->Junit Test)

Finally, modifying source code in Eclipse automatically triggers hot code replace to a running instance, allowing you to immediately test your changes


Instructions to run using Docker and docker-compose
===================================================

It is possible to do a 'one-touch' installation of Fineract using containers (AKA "Docker").
Fineract now packs the mifos community-app web UI in it's docker deploy.
You can now run and test fineract with a GUI directly from the combined docker builds.
This includes the database running in a container.

As Prerequisites, you must have `docker` and `docker-compose` installed on your machine; see
[Docker Install](https://docs.docker.com/install/) and
[Docker Compose Install](https://docs.docker.com/compose/install/).

Alternatively, you can also use [Podman](https://github.com/containers/libpod)
(e.g. via `dnf install podman-docker`), and [Podman Compose](https://github.com/containers/podman-compose/)
(e.g. via `pip3 install podman-compose`) instead of Docker.

Now to run a new Fineract instance you can simply:

1. `git clone https://github.com/ncfprojects/cchpl_backend.git ; cd fineract`
1. for windows, use `git clone https://github.com/ncfprojects/cchpl_backend.git --config core.autocrlf=input ; cd fineract`
1. `docker-compose build`
1. `docker-compose up -d`
1. fineract (back-end) is running at https://localhost:8443/fineract-provider/
1. wait for https://localhost:8443/fineract-provider/actuator/health to return `{"status":"UP"}`
1. you must go to https://localhost:8443 and remember to accept the self-signed SSL certificate of the API once in your browser, otherwise  you get a message that is rather misleading from the UI.
1. community-app (UI) is running at http://localhost:9090/?baseApiUrl=https://localhost:8443/fineract-provider&tenantIdentifier=default
1. login using default _username_ `mifos` and _password_ `password`

The [`docker-compose.yml`](docker-compose.yml) will build the `fineract` container from the source based on the [`Dockerfile`](Dockerfile).  You could change that to use the pre-built container image instead of having to re-build it.

https://hub.docker.com/r/apache/fineract has a pre-built container image of this project, built continuously.

You must specify the MySQL tenants database JDBC URL by passing it to the `fineract` container via environment
variables; please consult the [`docker-compose.yml`](docker-compose.yml) for exact details how to specify those.
_(Note that in previous versions, the `mysqlserver` environment variable used at `docker build` time instead of at
`docker run` time did something similar; this has changed in [FINERACT-773](https://issues.apache.org/jira/browse/FINERACT-773)),
and the `mysqlserver` environment variable is now no longer supported.)_


Instructions to run on Kubernetes
=================================

General Clusters
----------------

You can also run Fineract using containers on a Kubernetes cluster.
Make sure you set up and connect to your Kubernetes cluster.
You can follow [this](https://cwiki.apache.org/confluence/display/FINERACT/Install+and+configure+kubectl+and+Google+Cloud+SDK+on+ubuntu+16.04) guide to set up a Kubernetes cluster on GKE. Make sure to replace `apache-fineract-cn` with `apache-fineract`

Now e.g. from your Google Cloud shell, run the following commands:

1. `git clone https://github.com/ncfprojects/cchpl_backend.git; cd fineract/kubernetes`
1. `./kubectl-startup.sh`

To shutdown and reset your Cluster, run:

    ./kubectl-shutdown.sh

Using Minikube
--------------

Alternatively, you can run fineract on a local kubernetes cluster using [minikube](https://minikube.sigs.k8s.io/docs/).
As Prerequisites, you must have `minikube` and `kubectl` installed on your machine; see
[Minikube & Kubectl install](https://kubernetes.io/docs/tasks/tools/install-minikube/).

Now to run a new Fineract instance on Minikube you can simply:

1. `git clone https://github.com/ncfprojects/cchpl_backend.git ; cd fineract/kubernetes`
1. `minikube start`
1. `./kubectl-startup.sh`
1. `minikube service fineract-server --url --https`
1. Fineract is now running at the printed URL (note HTTP), which you can check e.g. using:

    http --verify=no --timeout 240 --check-status get $(minikube service fineract-server --url --https)/fineract-provider/actuator/health

To check the status of your containers on your local minikube Kubernetes cluster, run:

    minikube dashboard

You can check Fineract logs using:

    kubectl logs deployment/fineract-server

To shutdown and reset your cluster, run:

    ./kubectl-shutdown.sh

To shutdown and reset your cluster, run:

    minikube ssh

    sudo rm -rf /mnt/data/



Instructions to download Gradle wrapper
============
The file gradle/wrapper/gradle-wrapper.jar binary is checked into this projects Git source repository,
but won't exist in your copy of the Fineract codebase if you downloaded a released source archive from apache.org.
In that case, you need to download it using the commands below:

    wget --no-check-certificate -P gradle/wrapper https://github.com/apache/fineract/raw/develop/gradle/wrapper/gradle-wrapper.jar

(or)

    curl --insecure -L https://github.com/apache/fineract/raw/develop/gradle/wrapper/gradle-wrapper.jar > gradle/wrapper/gradle-wrapper.jar


Instructions to run Apache RAT (Release Audit Tool)
============
1. Extract the archive file to your local directory.
2. Run `./gradlew rat`. A report will be generated under build/reports/rat/rat-report.txt


Instructions to enable ActiveMQ
============
Messaging configuration is disabled by default. If you want to enable it and register some message listeners, application needs to be started with the proper Spring profile, ie `-Dspring.profiles.active=activeMqEnabled` (or one of the other Spring ways to configure it).


Checkstyle and Spotless
============

This project enforces its code conventions using [checkstyle.xml](config/checkstyle/checkstyle.xml) through Checkstyle and [fineract-formatting-preferences.xml](config/fineract-formatting-preferences.xml) through Spotless. They are configured to run automatically during the normal Gradle build, and fail if there are any violations detected. You can run the following command to automatically fix spotless violations:

    `./gradlew spotlessApply`

Since some checks are present in both Checkstyle and Spotless, the same command can help you fix some of the Checkstyle violations (but not all, other Checkstyle violations need to fixed manually).

You can also check for Spotless violations (only; but normally don't have to, because the regular build full already includes this anyway):

    `./gradlew spotlessCheck`

We recommend that you configure your favourite Java IDE to match those conventions. For Eclipse, you can go to
Window > Java > Code Style and import our [config/fineractdev-formatter.xml](config/fineractdev-formatter.xml) under formatter section and [config/fineractdev-cleanup.xml](config/fineractdev-cleanup.xml) under Clean up section. The same fineractdev-formatter.xml configuration file (that can be used in Eclipse IDE) is also used by Spotless to both check for violations and autoformat code on the CLI.
You could also use Checkstyle directly in your IDE (but you don't neccesarily have to, it may just be more convenient for you).  For Eclipse, use https://checkstyle.org/eclipse-cs/ and load our checkstyle.xml into it, for IntelliJ you can use [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea).


Code Coverage Reports
============

The project uses Jacoco to measure unit tests code coverage, to generate a report run the following command:

    `./gradlew clean build jacocoTestReport`

Generated reports can be found in build/code-coverage directory.

Apache Fineract Platform API
============

The API for Fineract is documented in [apiLive.htm](fineract-provider/src/main/resources/static/api-docs/apiLive.htm), and the [apiLive.htm can be viewed on Fineract.dev](https://demo.fineract.dev/fineract-provider/api-docs/apiLive.htm "API Documentation").  If you have your own Fineract instance running, you can find this documentation under [/fineract-provider/api-docs/apiLive.htm](https://localhost:8443/fineract-provider/api-docs/apiLive.htm).

The Swagger documentation (work in progress; see [FINERACT-733](https://issues.apache.org/jira/browse/FINERACT-733)) can be accessed under [/fineract-provider/swagger-ui/index.html](https://localhost:8443/fineract-provider/swagger-ui/index.html) and [live Swagger UI here on Fineract.dev](https://demo.fineract.dev/fineract-provider/swagger-ui/index.html).

Apache Fineract supports client code generation using [Swagger Codegen](https://github.com/swagger-api/swagger-codegen) based on the [OpenAPI Specification](https://swagger.io/specification/).  For more instructions on how to generate the client code, check [docs/developers/swagger/client.md](docs/developers/swagger/client.md).


API clients (Web UIs, Mobile, etc.)
============

* https://github.com/openMF/community-app/ is the "traditional" Reference Client App Web UI for the API offered by this project
* https://github.com/openMF/web-app is the next generation UI rewrite also using this project's API
* https://github.com/openMF/android-client is an Android Mobile App client for this project's API
* https://github.com/openMF has more related proejcts


Online Demos
============

* [fineract.dev](https://www.fineract.dev) always runs the latest version of this code
* [demo.mifos.io](https://demo.mifos.io) A demo account is provided for users to experience the functionality of the Community App.  Users can use "mifos" for USERNAME and "password" for PASSWORD (without quotation marks).
* [Swagger-UI Demo video](https://www.youtube.com/watch?v=FlVd-0YAo6c) This is a demo video for Swagger-UI documentation, more information [here](https://github.com/apache/fineract#swagger-ui-documentation).



Developers
============
Please see <https://cwiki.apache.org/confluence/display/FINERACT/Contributor%27s+Zone> for the developers wiki page.

Please refer to <https://cwiki.apache.org/confluence/display/FINERACT/Fineract+101> for the first-time contribution to this project.

Please see <https://cwiki.apache.org/confluence/display/FINERACT/How-to+articles> for technical details to get started.

Please visit [our JIRA Dashboard](https://issues.apache.org/jira/secure/Dashboard.jspa?selectPageId=12335824) to find issues to work on, see what others are working on, or open new issues.


Video Demonstration
============

Apache Fineract / Mifos X Demo (November 2016) - <https://www.youtube.com/watch?v=h61g9TptMBo>

Swagger-UI Documentation
============

We use Swagger-UI to generate and maintain our API documentation, you can see the demo video [here](https://www.youtube.com/watch?v=FlVd-0YAo6c) or a live version
[here](https://demo.fineract.dev/fineract-provider/swagger-ui/index.html). If you interested to know more about Swagger-UI you can check their [website](https://swagger.io/).

Governance and Policies
=======================

[Becoming a Committer](https://cwiki.apache.org/confluence/display/FINERACT/Becoming+a+Committer)
documents the process through which you can become a committer in this project.

Logging Guidelines
------------------
* We use [SLF4J](http://www.slf4j.org) as our logging API.
* Never, ever, use `System.out` and `System.err` or `printStackTrace()` anywhere, but always `LOG.info()` or `LOG.error()` instead.
* Use placeholder (`LOG.error("Could not... details: {}", something, exception)`) and never String concatenation (`LOG.error("Could not... details: " + something, exception)`)
* Which Log Level is appropriate?
  * `LOG.error()` should be used to inform an "operator" running Fineract who supervises error logs of an unexpected condition.  This includes technical problems with an external "environment" (e.g. can't reach a database), and situations which are likely bugs which need to be fixed in the code.  They do NOT include e.g. validation errors for incoming API requests - that is signaled through the API response - and does (should) not be logged as an error.  (Note that there is no _FATAL_ level in SLF4J; a "FATAL" event should just be logged as an _ERROR_.)
  * `LOG.warn()` should be using sparingly.  Make up your mind if it's an error (above) - or not!
  * `LOG.info()` can be used notably for one-time actions taken during start-up.  It should typically NOT be used to print out "regular" application usage information.  The default logging configuration always outputs the application INFO logs, and in production under load, there's really no point to constantly spew out lots of information from frequently traversed paths in the code about what's going on.  (Metrics are a better way.)  `LOG.info()` *can* be used freely in tests though.
  * `LOG.debug()` can be used anywhere in the code to log things that may be useful during investigations of specific problems.  They are not shown in the default logging configuration, but can be enabled for troubleshooting.  Developers should typically "turn down" most `LOG.info()` which they used while writing a new feature to "follow along what happens during local testing" to `LOG.debug()` for production before we merge their PRs.
  * `LOG.trace()` is not used in Fineract.

