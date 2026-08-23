package com.ashfaq.example.executiontimesample;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
//@Target(ElementType.METHOD): This annotation can only be applied to methods.
//@Retention(RetentionPolicy.RUNTIME): The annotation will be available at runtime so that AOP can use it.