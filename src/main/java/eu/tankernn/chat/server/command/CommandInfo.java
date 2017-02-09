package eu.tankernn.chat.server.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
	public String name();
	
	public String permission();
	
	public String desc();
	
	public int minArg() default 0;
}
