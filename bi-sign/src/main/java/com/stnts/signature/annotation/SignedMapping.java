package com.stnts.signature.annotation;


import com.stnts.signature.service.BaseSignedService;

import java.lang.annotation.*;

/**
 * The annotation indicates that the RESTful api needs to be signed
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface SignedMapping {
    Class<?> value() default BaseSignedService.class;
}
