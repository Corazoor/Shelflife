# Shelflife

There will be some warnings for missing types.
This is partly intended (to demonstrate error handling) and in part because the NewYearsEve Module is missing.

## How to use modules.
Compile the two Module projects SuperDuperMarktSQL and SuperDuperMarktSilvester. 
Create (two) jar files from these projects only containing the project specific classes.
Put these jar files into the "modules" folder in the base project. 
Edit the settings.properties to uncomment the two provided lines.
The mysql-connector-j librarie should be reachable via classpath-

The Project was created in IntelliJ, project files are included.
Predefined artifacts to build the jar files are included.

The "Main" RunProfile already adds the mysql-connector-j librarie to the classpath,
but expects it to be in the "modules" folder as well.