## Spring Security LDAP

This demo project shows how to perform LDAP authentication with Spring Security.
It was forked from [this repo](https://github.com/wearearima/spring-boot-ldap-login).

### Relevant Article: 

- [Learn Spring Security](http://github.learnspringsecurity.com)
- [Spring Security – security none, filters none, access permitAll](https://www.baeldung.com/security-none-filters-none-access-permitAll)
- [Intro to Spring Security LDAP](https://www.baeldung.com/spring-security-ldap)

### Running the project

- You MUST set `--add-exports java.naming/com.sun.jndi.ldap=ALL-UNNAMED` for JVM options. 
  - Reference `.run/SampleLDAPApplication.run.xml` and [this stackoverflow post](https://stackoverflow.com/questions/73202366/ldapctxfactory-because-module-java-naming-does-not-export-com-sun-jndi-ldap-to-u/73803225).
- Run `SampleLDAPApplication.java` to start up Spring Boot with a Tomcat container.
- Run `docker compose up -d` to provision the openLDAP server.
    - [Bitnami Container Image](https://hub.docker.com/r/bitnami/openldap/)
- Once started, open http://localhost:8080 to view the publicly available Home Page.
- Navigate to http://localhost:8080/secure to trigger authentication
  - Authorized User: `authorized:bitnami1`
  - Unauthorized User: `unauthorized:bitnami1`

### Helpful Commands

Remove the container volume that stores config info when changing the `.ldif` file with:

```shell
docker-compose down && docker volume rm openldap_data && docker-compose up -d 
```

```shell
ldapsearch -x -H ldap://localhost:1389 -b dc=example,dc=org -D "cn=admin,dc=example,dc=org" -w adminpassword # View loaded .ldif file
```

```shell
ldapsearch -x -H ldap://localhost:1389 -b "cn=admin,ou=groups,dc=example,dc=org" # View members of 'admin' group
```

Verify that user authentication with the following commands:

```shell
ldapwhoami -vvv -h localhost:1389 -D "cn=authorized,ou=people,dc=example,dc=org" -x -w bitnami1 # Login as authorized

ldapwhoami -vvv -h localhost:1389 -D "cn=unauthorized,ou=people,dc=example,dc=org" -x -w bitnami1 # Login as unauthorized
```