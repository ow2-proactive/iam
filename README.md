IAM
============================
![N|Solid](https://try.activeeon.com/assets/image/proactive-arrow-150.png)

ProActive IAM (Identitiy and Access Management) microservice, built on top of CAS (Centralized Authentication Service).
(see https://www.apereo.org/projects/cas and https://github.com/apereo/cas).

## Versions
* IAM `8.3.0-SNAPSHOT`
* CAS `5.3.2`

## Requirements

* JDK 1.8+

## Configuration

IAM is built as a Spring boot application (using the CAS gradle overlay). The main configuration file `application.properties` is located under the `cas` module, in `src/main/resources`. Further configuration files are located under the directory `src/main/resources/config/iam` .

## Extra packages

In addition to CAS, IAM microservice implements an embedded LDAP server based on Apache Directory Server (http://directory.apache.org/apacheds/). This LDAP server is used as the default identity backend of CAS. The configuration properties of the embedded LDAP server are in the configuration file `application.properties`, whereas the identities are stored in  `src/main/resources/config/iam/ldap/identities.ldif`.

## Build
To build the IAM application:
```bash
./gradlew clean build
```

## Run
Run IAM in the dev environment using gradle:
```bash
./gradlew run
```

Or:

## Executable WAR
IAM uses the notion of Spring Boot profiles to run (as an executable WAR) in different environments:

* Get the executable war file built under `cas/build/libs/iam-xx.war`
* Run IAM using the default profile:
```bash
java -jar iam-xx.war
```
When using the default profile, IAM uses the configuration file `WEB-INF/classes/application-default.properties` located in the jar archive.  The remaining configuration files are provided under `WEB-INF/classes/config/iam/`


* Run IAM using ProActive profile:
```bash
java -Dpa.scheduler.home=${path_to_scheduler_home} -jar iam-xx.war --spring.profiles.active=proactive  --spring.config.location=${path_to_scheduler_home}/config/iam/application-proactive.properties
```
When using the proactive profile, IAM uses the configuration file indicated by the parameter `--spring.config.location`.  The remaining configuration files are provided under `${path_to_scheduler_home}/config/iam/`.


* On a successful execution of the above methods, IAM will be available at the address defined by the property `cas.server.prefix` in the configuration file `application.properties`, by default it starts at:
  * `https://localhost:8444/iam`

* Access the IAM microservice using the credentials :
  * login: admin
  * password: admin

* Further credentials can be found in the file `src/main/resources/config/iam/identities.ldif` (under the `cas` module).

## Customize the configuration properties

You can edit and change one or more properties of your choice, For instance:

* iam.ldap.host=localhost_or_$hostname
* iam.ldap.port=11389_or_another_port
* iam.ldap.identities.file= some_absolute_path_to_identities.ldif
* In the ldif file you can add another user by adding the corresponding block. For instance:
```
dn: uid=toto,ou=users,dc=activeeon,dc=com
cn: toto
givenName: toto
sn: toto
uid: toto
userpassword: {SHA}N/omUzCtg+qoee+x4ttjgIls9jk=
role: user
objectClass: inetOrgPerson
objectClass: organizationalPerson
objectClass: extensibleObject
```
N.B. keep a blank line before and after this block (as for the other users)

* server.context-path=/iam_or_another_context
* server.port=8444_or_another_port
* cas.host.name: localhost_or_$hostname
* Check that the ports you chosen are not used. In linux, you can use 'lsof -i :port' and in Windows 'netstat -a -n -o | findstr :port'. These commands return nothing when the port is not used.

## Change the SSL Certificate

IAM uses a SSL certificate to secure its communications. The default certificate is located under `src/main/resources/config/iam/cas/keystore` (under the `cas` module). To generate a new certificate, use this command:

```bash
sudo keytool -genkeypair -keysize 2048 -alias ${keyStoreAlias} -keypass ${keyPass} -keystore ${keyStoreName} -storepass ${keyStorePass}`
```
N.B.: The CN of the generated certificate (the first param asked for) MUST be the same as the parameter `cas.host.name`, which means: localhost or the hostname.

*   Put the new certificate in some accessible location of your choice
*   Edit the following properties with respect to the generated SSL certificate:
 *   server.ssl.key-store=$absolute_path/keyStoreName
 *   server.ssl.key-store-password=$keyStorePass
 *   server.ssl.key-password=$keyPass
 *   cas.authn.pac4j.saml[0].keystorePath=$absolute_path/keyStoreName
 *   cas.authn.pac4j.saml[0].keystorePassword=$keyStorePass
 *   cas.authn.pac4j.saml[0].privateKeyPassword=$keyPass

N.B.: IAM must be restarted to load the new certificate.
