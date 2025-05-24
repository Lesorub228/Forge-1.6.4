// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE })
public @interface API {
    String owner();
    
    String provides();
    
    String apiVersion();
}
