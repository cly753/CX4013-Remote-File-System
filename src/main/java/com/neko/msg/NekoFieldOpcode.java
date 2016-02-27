package com.neko.msg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.logging.Level;
import java.util.logging.Logger;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NekoFieldOpcode {
}
