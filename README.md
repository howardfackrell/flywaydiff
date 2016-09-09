#Flywaydiff
See what migrations have been run in one schema, but not in another


##Usage
The jar is executable. It expects 2 paramters: the 2 jdbc url's for the databases you wish to compare
```
java -jar ./target/flywaydiff-1.0-SNAPSHOT.jar jdbc:oracle:thin:user/password@dev.example.com:1521:dev jdbc:oracle:thin:scott/tiger@prod.example.com:1521:prod
```

##Simpler Usage
You can add url aliases to a properties file called env.properties on the classpath

```
dev=jdbc:oracle:thin:user/password@dev.example.com:1521:dev
qa=jdbc:oracle:thin:user/password@qa.example.com:1521:qa
prod=jdbc:oracle:thin:scott/tiger@prod.example.com:1521:prod
```

and then run the program with the aliases

```
java -jar ./target/flywaydiff-1.0-SNAPSHOT.jar dev prod
```

If you place this properties file in src/main/resources/env.properties before packaging it will be included in the jar,
but the .gitignore prevents it from being committed to the repo

##Note
This is currently only set up to work with oracle databases