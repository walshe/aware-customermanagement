


h2


hikari connection pool?


ddl-auto

http://localhost:8080/v2/api-docs



INSERT INTO CUSTOMER (birth_date, gender, name) VALUES('2000-01-01', 'MALE','Hello');


- build and execute a war
- write a repository IT test
- mock test for service test
- unit test for Customer obj
- CORS
- test reports and code coverage

lombok builder

select DATEDIFF(YEAR,   '1977-08-30', CURRENT_DATE())
select avg(DATEDIFF(YEAR,   birth_date, CURRENT_DATE())) as avg_age from customer;

problems:
    stacktrace appearing in error response
    data.sql not running

commands:

run app

    ./mvnw spring-boot:run    

run tests
    
    ./mvnw test
