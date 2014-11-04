A fork for contributions of Humboldt University/Berlin to the ALISE project of the Carl v. Ossietzky University. For information about the project, instructions and todos see GitHub project page.

== Change milestones ==
  * increasing portability and JEE7 compliance: the project has reviewed in terms of JEE7 compatibility (in both matching of requirements and usage of advantages of the technology) and moved from tomee server (with an undocumented patch) to GlassFish 4.0.
  * maximization of usage of Java language features (including maximization of type saftly and the necessary review of architecture and modularization), porting to Java 7
  * stricter unit testing (EJBs with Apache OpenEJB 4.6)

== Getting started ==
The main development activity is as usual (for git projects) going on on the `master` branch (again).

Currently there's a bunch of `python` scripts which serve a very straight-forward setup. A recommended set of commands is:

    git clone https://github.com/richtekp/pgalise.git
    cd pgalise
    sudo python scripts/bootstrap_prequisites.py # currently we use the system package manager as much as possible, consider setting up a lxc or VirtualBox for a Debian 7.4 or Ubuntu 14.04
    python scripts/bootstrap.py # sets up a database as well
    python scripts/start_db.py
    mvn clean install # add -DskipTests=true if test failures occur
    python scripts/deploy_glassfish.py # interactively asks you to specify a location for an installation of an Oracle GlassFish instance
    # point your browser to http://localhost:8081/controlCenter

Furthermore you might be interested in the `osm_postgis_transform.py` script which facilitates the creation of PostGIS databases from OSM data, see [python-essentials' README](https://github.com/krichter722/python-essentials) and/or `python osm_postgis_transform.py --help` for details and instructions.

== Further information ==
If you have any questions about the cooperation between Humboldt University and Carl v. Ossietzky University please contact the institute for databases and information system (DBIS) of the Humboldt University (mjsax@informatik.hu-berlin.de or richtekp@informatik.hu-berlin.de).
