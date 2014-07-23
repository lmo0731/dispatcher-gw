/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.conf;

import static java.lang.annotation.ElementType.FIELD;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author LMO
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigName {

    String value() default "##default";

    boolean required() default false;
}
