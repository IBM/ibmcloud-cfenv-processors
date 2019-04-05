## Description
Creates an app which tests the spring cloud connector to cloudant.

## Setup
1. Create ibm cloud account: https://cloud.ibm.com/
2. Install ibm cloud CLI: https://cloud.ibm.com/docs/cli/reference/ibmcloud?topic=cloud-cli-install-ibmcloud-cli#install_use
3. ibm clound login: ibmcloud login --sso
4. cloud foundry login: ibmcloud target --cf

## Build
./mvnw clean package -Dmaven.test.skip=true
(not doing any testing)

## Deploy
bx cf push -b java_buildpack -p target/testapp-0.0.1-SNAPSHOT.jar TerrenceTestApp

## Verify
1. navigate to http://terrencetestapp.mybluemix.net/test
2. check log: bx cf logs YourAppname --recent
