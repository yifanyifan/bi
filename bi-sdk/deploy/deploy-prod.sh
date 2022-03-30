mvn -f ../../bi-common/pom.xml clean install
mvn -f ../../bi-sql/pom.xml clean install
mvn -f ../pom.xml clean install -P prod

sshpass -p 'cW6kyivy=4tmD' scp ../target/bi-sdk-1.0.jar root@10.0.45.237:/root/Dockerfiles/bi-prod/sdk/bi-sdk-1.0.jar
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi-prod/sdk; docker build -t dchub.stnts.com/dt-prod/bi-sdk:0.8 .;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi-prod/sdk; docker push dchub.stnts.com/dt-prod/bi-sdk:0.8;"

mvn -f ../pom.xml clean compile -P dev