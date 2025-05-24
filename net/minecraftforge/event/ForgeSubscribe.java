// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ForgeSubscribe {
    EventPriority priority() default EventPriority.NORMAL;
    
    boolean receiveCanceled() default false;
}
