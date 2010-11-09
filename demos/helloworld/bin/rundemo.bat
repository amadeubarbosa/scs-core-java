@ECHO OFF
ECHO   [ INFO ]
ECHO   The CLASSPATH variable is: %CLASSPATH%
ECHO   [ INFO ]
SETLOCAL
SET DEFAULT_SCS_HOME=C:\Temp
IF NOT DEFINED SCS_HOME ECHO   Using the following SCS installation %DEFAULT_SCS_HOME%
IF NOT DEFINED SCS_HOME SET SCS_HOME=%DEFAULT_SCS_HOME%
ECHO   [ INFO ] 
ECHO   Using the following PATH: %PATH%
ECHO   [ Launching the demo: %1 ... ]
SET CLASSPATH="%SCS_HOME%\avalon-framework-4.1.5.jar;%SCS_HOME%\jacorb-2.3.0.jar;%SCS_HOME%\logkit-1.2.jar;%SCS_HOME%\scs-idl-jacorb-1.2.0.jar;%SCS_HOME%\scs-core-1.2.0.jar;%SCS_HOME%\scs-demos-helloworld-1.2.0.jar"
java -Djava.library.path=%SCS_HOME% -Djava.endorsed.dirs=%SCS_HOME% -Djacorb.home=%SCS_HOME% -Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton %*
ENDLOCAL
