#JDK基础镜像
FROM dchub.stnts.com/zhangliang/java/alpine-jdk:1.8.0_212

#维护
MAINTAINER zhangl

WORKDIR /webapp

ADD bi-plugin-1.0.jar ./

#set environment
ENV JVM_XMS="1g" \
    JVM_XMX="2g" \
    JVM_XMN="1g" \
    JVM_MS="128m" \
    JVM_MMS="320m" \
    TOMCAT_ACCESSLOG_ENABLED="false" \
    TZ="Asia/Shanghai"

ENV HOSTNAME bi-plugin

#开放端口
EXPOSE 18884

ENTRYPOINT ["nohup", "java" ,"-jar", "bi-plugin-1.0.jar", "-Xms512M", "-Xmx1G",">/dev/null", "2>&1", "&"] 
