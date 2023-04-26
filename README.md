## Spring Security LDAP

This module contains articles about Spring Security LDAP

### The Course

The "Learn Spring Security" Classes: http://github.learnspringsecurity.com

### Relevant Article: 

- [Spring Security – security none, filters none, access permitAll](https://www.baeldung.com/security-none-filters-none-access-permitAll)
- [Intro to Spring Security LDAP](https://www.baeldung.com/spring-security-ldap)

### Notes

- the project uses Spring Boot - simply run 'SampleLDAPApplication.java' to start up Spring Boot with a Tomcat container and embedded LDAP server.
- Run `docker compose up -d` to provision the openLDAP server.
- Once started, open 'http://localhost:8080'
- This will display the publicly available Home Page
- Navigate to 'Secure Page' to trigger authentication
- Username: 'user01', password: 'bitnami1'
- This will authenticate you, but will NOT display your allocated roles (as defined in the 'ldap-server.ldif' file)

### Misc Commands

Remove the container volume that stores config info when changing the `ldif` file with:

```shell
docker-compose down && docker volume rm openldap_data ; docker-compose up -d
```


[Container image](https://hub.docker.com/r/bitnami/openldap/)

```shell
docker exec etav_web-openldap-1 ldapsearch -x -H ldap://localhost:1389 -b dc=example,dc=org -D "cn=admin,dc=example,dc=org" -w adminpassword
```

```shell
ldapwhoami -vvv -h localhost:1389 -D "cn=admin,ou=people,dc=example,dc=org" -x -w adminpassword # Login as admin

ldapwhoami -vvv -h localhost:1389 -D "cn=user01,ou=people,dc=example,dc=org" -x -w password1 # Login as user01

ldapwhoami -vvv -h localhost:1389 -D "cn=user02,ou=people,dc=example,dc=org" -x -w password2 # Login as user02
```

[LDAP demo project](https://github.com/wearearima/spring-boot-ldap-login)

```shell
ldapsearch -x -b "dc=example,dc=org" -H ldap://0.0.0.0:1389
```

```shell

ldapwhoami -vvv -h localhost:1389 -D "cn=testuser,ou=people,dc=example,dc=org" -x -w password2 # Login as testuser
```

```shell
ldapsearch -x -H ldap://localhost:1389 -b "ou=groups,dc=example,dc=org" "(&(objectClass=groupOfNames)(cn=admin)(groupMembershipRoles=*))" groupMembershipRoles
```
