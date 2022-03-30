package com.stnts.bi.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
@SpringBootApplication
@ComponentScan({"com.stnts.bi.schedule"})
@EnableDiscoveryClient
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class ScheduleBootApplication {

    public  static void main(String [] args){

        SpringApplication.run(ScheduleBootApplication.class,args);

    }
}

