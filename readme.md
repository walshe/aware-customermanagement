
- build and execute a war

problems:
    stacktrace appearing in error response

# CustomerManagement API

Run app

    ./mvnw spring-boot:run    

Access Swagger UI

    http://localhost:8080/swagger-ui/

Access H2 Console (username: sa, password: password)

    http://localhost:8080/h2-console


Run tests and generate html report 
(generated at target/site/surefire-report.html) and code coverage report (at target/site/jacoco/index.html) :

    ./mvnw surefire-report:report

Just run tests with no reports
    
    ./mvnw test


### TODO
- Currently api errors have a really verbose stacktrace field which shows entire stack. Having some issues removing
- Need to package the app into an executable jar