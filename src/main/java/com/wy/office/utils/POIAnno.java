package com.wy.office.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface POIAnno {

    public int cellIndex() ;

    public String cellName() default "";
}
