mvn -f ../../bi-common/pom.xml clean install
mvn -f ../pom.xml clean install -P test

sshpass -p 'cW6kyivy=4tmD' scp ../target/bi-schedule-1.0.jar root@10.0.45.237:/root/Dockerfiles/bi/schedule/bi-schedule-1.0.jar
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi/schedule; docker build -t dchub.stnts.com/zhangliang/project/bi-schedule:0.1 .;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.45.237 "cd /root/Dockerfiles/bi/schedule; docker push dchub.stnts.com/zhangliang/project/bi-schedule:0.1;"
sshpass -p 'cW6kyivy=4tmD' ssh root@10.0.44.34 "cd /root/dockerfiles/bi/bi-schedule; kubectl delete -f bi-schedule.yaml; kubectl create -f bi-schedule.yaml;"

mvn -f ../pom.xml clean compile -P dev