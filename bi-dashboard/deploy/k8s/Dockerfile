#JDK基础镜像
FROM dchub.stnts.com/zhangliang/java/alpine-jdk:1.8.0_212

#维护
MAINTAINER ty.liu


WORKDIR /webapp

ADD bi-dashboard-1.0.jar ./

#set environment
ENV JVM_XMS="1g" \
    JVM_XMX="2g" \
    JVM_XMN="1g" \
    JVM_MS="128m" \
    JVM_MMS="320m" \
    TOMCAT_ACCESSLOG_ENABLED="false" \
    TZ="Asia/Shanghai"

ENV HOSTNAME ms-base-admin

#开放端口
EXPOSE 18886

ENTRYPOINT ["nohup", "java" ,"-jar", "bi-dashboard-1.0.jar", "-Xms512M", "-Xmx2G", ">/dev/null", "2>&1", "&"] 
