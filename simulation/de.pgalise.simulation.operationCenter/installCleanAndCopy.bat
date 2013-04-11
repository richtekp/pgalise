call mvn clean install -DskipTests=true

call xcopy "target\operationCenter-2.0-SNAPSHOT.war" "C:\TomEE\apache-tomee-webprofile-1.5.1\webapps\operationCenter.war" /Y
