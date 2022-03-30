package com.stnts.signature.annotation;


import com.stnts.signature.service.BaseSignedService;
import com.stnts.signature.util.RedisUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * The annotation is in the Application class and is used to scan other implementation classes
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({BaseSignedService.class, RedisUtil.class})
public @interface SignedScan {
}
