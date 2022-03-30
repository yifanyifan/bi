mvn -f ../../bi-common/pom.xml clean install
mvn -f ../../bi-sql/pom.xml clean install
mvn -f ../../bi-sign/pom.xml clean install
mvn -f ../pom.xml clean install -P prod

sshpass -p 'cW6kyivy=4tmD' scp ../target/bi-data-management-1.0.jar root@10.0.45.237:/root/Dockerfiles/bi/datamanagement/bi-data-management-1.0.jar
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi/datamanagement; docker build -t dchub.stnts.com/dt-prod/bi-data-management:0.1 .;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi/datamanagement; docker push dchub.stnts.com/dt-prod/bi-data-management:0.1;"
