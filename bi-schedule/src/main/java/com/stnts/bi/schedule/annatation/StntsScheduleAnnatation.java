package com.stnts.bi.schedule.annatation;


import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface StntsScheduleAnnatation {

    String name() default "";
    String cron() default "";
    String description() default "";
}
