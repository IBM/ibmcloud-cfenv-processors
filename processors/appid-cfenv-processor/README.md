## App ID CFEnv Processor

App ID CFEnv Processor is a library to set the properties in Spring Boot applications from the service entry in `VCAP_SERVICES` environment variable.

Spring Boot Starter to auto configure App ID in Spring Boot applications: https://github.com/IBM/appid-spring-boot-starter

**Note:** If you don't want the App ID CFEnv processor to look into the user provided services then you can disable it by passing the JVM system parameter -DCFENV_USER_PROVIDED_SERVICE_SEARCH_DISABLE=true
