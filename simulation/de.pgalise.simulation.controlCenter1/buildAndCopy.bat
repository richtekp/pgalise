call mvn build

call xcopy "target\controlCenter-0.0.1-SNAPSHOT.war" "C:\Jetty\jetty-distribution-7.6.5.v20120716\webapps\controlCenter.war" /Y
