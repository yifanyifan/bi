mvn -f ../pom.xml clean install

sshpass -p 'cW6kyivy=4tmD' scp ../target/bi-admin-1.0.jar root@10.0.45.237:/root/Dockerfiles/bi/admin/bi-admin-1.0.jar
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi/admin; docker build -t dchub.stnts.com/dt-prod/bi-admin:0.1 .;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi/admin; docker push dchub.stnts.com/dt-prod/bi-admin:0.1;"
