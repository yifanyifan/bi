mvn -f ../../bi-common/pom.xml clean install
mvn -f ../../bi-sql/pom.xml clean install
mvn -f ../pom.xml clean install -P test

sshpass -p 'cW6kyivy=4tmD' scp ../target/bi-sdk-1.0.jar root@10.0.45.237:/root/Dockerfiles/bi/sdk/bi-sdk-1.0.jar
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi/sdk; docker build -t dchub.stnts.com/zhangliang/project/bi-sdk:0.1 .;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi/sdk; docker push dchub.stnts.com/zhangliang/project/bi-sdk:0.1;"
#sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.117 "cd /root/dockerfiles/bi/bi-sdk; kubectl delete -f bi-sdk.yaml; kubectl create -f bi-sdk.yaml;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.44.34 "cd /root/dockerfiles/bi/bi-sdk; kubectl apply -f bi-sdk.yaml --record; kubectl rollout restart deployment/bi-sdk;"

mvn -f ../pom.xml clean compile -P dev