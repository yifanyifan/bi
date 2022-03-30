package com.stnts.signature.annotation;

import java.lang.annotation.*;

/**
 * The annotation indicates that the field is a appId
 */
@Target({ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SignedAppId {
}
