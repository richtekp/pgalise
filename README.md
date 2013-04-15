# PGAlise - Smarter Cities

In a nutshell this project is the outcome of a student group from the Carl v. Ossietzky University to generate synthetic sensor data comprising the most important data from traffic, weather and energy. The generated data are based upon a distributed simulation of a fictional city that can be provided with real world data. For instance Oldenburg, a city in Lower Saxony in Germany, provides the road and public transportation network of the used sample city.

[Visit our website](http://pg-alise.com) to gain more information and full documentation!

## Install
* Install first [Apache Maven](http://maven.apache.org/)
* Checkout the source: `git://github.com/luxmeter/pgalise.git`
* Run in the root directory `mvn -DskipTests clean install`.
   
## Getting Started
* Install the project
* Download and install [Odysseus](http://odysseus.informatik.uni-oldenburg.de:8090/display/ODYSSEUS/Odysseus+Home)
* [Configure Odysseus](https://github.com/luxmeter/pgalise/wiki/Configure-Odysseus)
* Download our [Derby-DB Database Dump](http://www.pg-alise.com/openRes/derby_database.tar.gz)
* Extract the downloaded archive in a directory of your choice
* Run the start script with `./startderby.sh`
* Download [TomEE Web Profile v1.5.0](http://archive.apache.org/dist/openejb/openejb-4.5.0/apache-tomee-1.5.0-webprofile.tar.gz)
* Extrac TomEE in a dirctory of your choice
* Change to the directory of TomEE
* Apply our patch (tomee.patch) using git-apply: `git apply --ignore-space-change --ignore-whitespace tomee.patch`
* Copy ear/target/simulation into the apps directory in your TomEE installation
* Start the server `./bin/catalina.sh run`
* For the ControlCenter open the URL `http://localhost:8080/controlCenter`
* For the OperationCenter open the URL `http://localhost:8080/operationCenter`

* Check out the [docs](http://pg-alise.com)

## To Do
* (R1) OperationCenter should be configured over the ConfigReader
* (R2) HQF-Components should retrieve their database connection through the EJB container
* (R3) Parser of the public transportation issues (BusStop, GTFS) should retrieve its database connection through the EJB container
* (R4) HQF-Components in the OperationCenter should use JPA to handle database access
* (R5) Change the implementation of the Dashboard function (replace Cognos)
* (R6) Add the server address and the specfic ports of the Odysseus server in 'porperties.props' in 'de.pgalise.simulation.operationCenter'

## Known Issues
* Several integration tests fail or are ignored due to the anonymisation of ip addresses
* Control- and OperationCenter won't work till R1 is fixed
* Dashboard in the OperationCenter won't work because Cognos is not available anymore

## Work Arounds
* Although you can't start a simulation through the web interface, you can start it programmatically. Take a look at the playground project! If any error occurs regarding the web interface, try to mock it.
   - Open the simulation.conf in the applib directory in the TomEE installation
   - Add: `simulation.configuration.server.ccc.mock=true` and `simulation.configuration.server.occ.mock=true`
* If your application can't handle the data throughput, try to adjust the simulation clock time intervall (see. playground/Main.java line 114)
