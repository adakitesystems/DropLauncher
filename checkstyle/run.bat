@ECHO OFF
CLS
ECHO --- Running checks.xml
ECHO.
java -jar checkstyle-7.5.1-all.jar -c checks.xml ../src/droplauncher/
@ECHO ON
