@ECHO OFF
CLS
ECHO --- Running checks.xml
ECHO.
java -jar checkstyle-7.1.2-all.jar -c checks.xml ../src/
@ECHO ON
