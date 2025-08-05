package com.jobnote.global.annotation.swagger;

import org.springframework.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSuccessResponseExplanation {
	HttpStatus status() default HttpStatus.OK;

	Class<?> responseClass() default EmptyClass.class;

	String description() default "";

	class EmptyClass {
	}
}
