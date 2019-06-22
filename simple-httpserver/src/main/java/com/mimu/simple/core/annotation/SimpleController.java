package com.mimu.simple.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: mimu
 * date: 2018/10/21
 */

/**
 * define the annotation
 *
 * @target: description the annotation goal , it has many choice which we can use ElementType to reference like the next example
 * ElementType.CONSTRUCTOR: to annotate the constructor method of a class
 * ElementType.FIELD: to annotate the filed of a class
 * ElementType.LOCAL_VARIABLE: to annotate the local variable in the method of a class
 * ElementType.METHOD: to annotate the method of a class
 * ElementType.PACKAGE: to annotate the package in of the class
 * ElementType.PARAMETER: to annotate the parameter in the method of a class
 * ElementType.TYPE: to annotate the type of the annotation this type can be calss interface(include annotation type) or enum
 * @Retention: describe the level of the annotation which we can use RetentionPolicy to reference like the next example
 * RetentionPolicy.SOURCE: it mean this annotation only exist in the source code and will be erase by the compiler
 * RetentionPolicy.CLASS: it mean this annotation can exist in the class file and source code while will be erase by the jvm
 * RetentionPolicy.RUNTIME: it mean this annotation can exist in jvm , class file and source code won't be erase
 * @Document: describe the annotation can generate in javadoc
 * @Inherited: describe the annotation can be inherited by subclass
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface SimpleController {
}
