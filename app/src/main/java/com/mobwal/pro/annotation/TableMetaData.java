package com.mobwal.pro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableMetaData {
    String pKey() default "id";
    String schema() default "dbo";
    String name();

    /**
     * Разрешать передавать объект на сервер
     * @return true - разрешено
     */
    boolean to() default true;

    /**
     * Объект разрешается забирать с сервера
     * @return true - разрещено
     */
    boolean from() default true;
}
