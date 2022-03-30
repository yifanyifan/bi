mvn -f ../../bi-common/pom.xml clean install
mvn -f ../../bi-sql/pom.xml clean install
mvn -f ../pom.xml clean install -P test
sshpass -p 'cW6kyivy=4tmD' scp ../target/bi-dashboard-1.0.jar root@10.0.6.33:/root/Dockerfiles/bi/dashboard/bi-dashboard-1.0.jar
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.33 "cd /root/Dockerfiles/bi/dashboard; docker build -t dchub.stnts.com/zhangliang/project/bi-dashboard:0.1 .;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.33 "cd /root/Dockerfiles/bi/dashboard; docker push dchub.stnts.com/zhangliang/project/bi-dashboard:0.1;"
#sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.8 "cd /root/dockerfiles/bi/bi-dashboard; kubectl delete -f bi-dashboard.yaml; kubectl create -f bi-dashboard.yaml;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.6.8 "cd /root/dockerfiles/bi/bi-dashboard; kubectl apply -f bi-dashboard.yaml --record; kubectl rollout restart deployment/bi-dashboard;"
mvn -f ../pom.xml clean compile -P dev