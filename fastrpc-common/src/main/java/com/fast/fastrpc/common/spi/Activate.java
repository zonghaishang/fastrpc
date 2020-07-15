package com.fast.fastrpc.common.spi;

/**
 * @author yiji
 * @version : Activate.java, v 0.1 2020-07-15
 */
public @interface Activate {

    String[] group() default {};

    String[] value() default {};

    int order() default 0;

}
