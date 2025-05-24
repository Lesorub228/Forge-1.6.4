// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

public final class Optional
{
    private Optional() {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface Method {
        String modid();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface Interface {
        String iface();
        
        String modid();
        
        boolean striprefs() default false;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface InterfaceList {
        Interface[] value();
    }
}
