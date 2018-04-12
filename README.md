IAM 
============================
![N|Solid](https://try.activeeon.com/assets/image/proactive-arrow-150.png)

ProActive IAM (Identitiy and Access Management) microservice, built on top of CAS (Centralized Authentication Service).
(see https://www.apereo.org/projects/cas and https://github.com/apereo/cas).

## Versions
* IAM `8.1.0-SNAPSHOT`
* CAS `5.2.3`

## Requirements

* JDK 1.8+

## Configuration

IAM is built as a Spring boot application (using the CAS gradle overlay). The main configuration file 'application.properties' is located under the `cas` module, in `src/main/resources`. Further configuration files are located in the 'config' directory.

## Extra packages

In addition to CAS, IAM microservice implements an embedded LDAP server based on Apache Directory Server (http://directory.apache.org/apacheds/). This LDAP server is used as the default identity backend of CAS. The configuration properties of the embedded LDAP server are located under the `cas` module, in `src/main/resources/config/iam/iam.properties`.

## Build
To build the IAM application:
```bash
./gradlew clean build
```

## Run
Run IAM from gradle:
```bash
./gradlew run
```
Or:
## Executable WAR
Run IAM as an executable WAR:

```bash
java -jar iam-xx.war
```
On a successful execution of the above methods, IAM will be available at:

* `https://localhost:8444/iam`

Access the IAM microservice using the following credentials:
- login: admin
- password: admin

Further credentials can be found in the file `src/main/resources/config/iam/identities.ldif` (under the `cas` module).


### Access the Admin Dashboard
IAM uses a SSL certificate to secure the access to the Administration Dashboard. This certificate is located under `src/main/resources/config/cas/keystore` (under the `cas` module).
To enable access to the Administration Dashboard, the SSL certificate must be recognized by the JVM used to run the IAM microservice (i.e., it must be added to Java KeyStore (JKS)). To do so, execute the following commands:
```bash
keytool -export -alias jetty -keystore $path_to_keystore -file keystore.crt
# N.B: The password of the keystore file is `activeeon`.
```
```bash
keytool -import -keystore $JAVA_HOME/jre/lib/security/cacerts -file $path_to_keystore.crt
# N.B: The default password of the JKS is `changeit`.
```

### Clear Gradle Cache
If you need to, on Linux/Unix systems, you can delete all the existing artifacts (artifacts and metadata)
Gradle has downloaded using:
```bash
# Only do this when absolutely necessary!
rm -rf $HOME/.gradle/caches/
```
Same strategy applies to Windows too, provided you switch `$HOME` to its equivalent in the above command.

### External Deployment
Some issues have to be fixed to deploy the war to a traditional servlet container.

