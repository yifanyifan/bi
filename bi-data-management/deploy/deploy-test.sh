mvn -f ../../bi-common/pom.xml clean install
mvn -f ../../bi-sql/pom.xml clean install
mvn -f ../../bi-sign/pom.xml clean install
mvn -f ../pom.xml clean install -P test

sshpass -p 'cW6kyivy=4tmD' scp ../target/bi-data-management-1.0.jar root@10.0.6.33:/root/Dockerfiles/bi/datamanagement/bi-data-management-1.0.jar
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.33 "cd /root/Dockerfiles/bi/datamanagement; docker build -t dchub.stnts.com/dt-test/bi-data-management:0.1 .;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.33 "cd /root/Dockerfiles/bi/datamanagement; docker push dchub.stnts.com/dt-test/bi-data-management:0.1;"
#sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.8 "cd /root/dockerfiles/bi/bi-data-management; kubectl delete -f bi-data-management.yaml; kubectl create -f bi-data-management.yaml;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.8 "cd /root/dockerfiles/bi/bi-data-management; kubectl apply -f bi-data-management-deployment.yaml --record; kubectl rollout restart deployment/bi-data-management;"

mvn -f ../pom.xml clean compile -P dev
