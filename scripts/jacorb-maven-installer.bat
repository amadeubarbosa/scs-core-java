
set VERSION=2.3.0

mvn install:install-file -DgroupId=org.jacorb -DartifactId=jacorb ^
    -Dversion=%VERSION% ^
    -Dpackaging=jar ^
    -Dfile=libs\jacorb-%VERSION%.jar ^
    -DpomFile=scripts\jacorb-pom.xml

mvn install:install-file -DgroupId=org.jacorb ^
    -DartifactId=jacorb-idl-compiler ^
    -Dversion=%VERSION% ^
    -Dpackaging=jar ^
    -Dfile=libs\jacorb-idl-%VERSION%.jar ^
    -DpomFile=scripts\jacorb-idl-compiler-pom.xml
