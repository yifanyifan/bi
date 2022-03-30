mvn -f ../../bi-common/pom.xml clean install
mvn -f ../../bi-sql/pom.xml clean install
mvn -f ../pom.xml clean install -P prod
sshpass -p 'cW6kyivy=4tmD' scp ../target/bi-dashboard-1.0.jar root@10.0.6.33:/root/Dockerfiles/bi/dashboard/bi-dashboard-1.0.jar
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.33 "cd /root/Dockerfiles/bi/dashboard; docker build -t dchub.stnts.com/dt-prod/bi-dashboard:0.1 .;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.33 "cd /root/Dockerfiles/bi/dashboard; docker push dchub.stnts.com/dt-prod/bi-dashboard:0.1;"
mvn -f ../pom.xml clean compile -P dev