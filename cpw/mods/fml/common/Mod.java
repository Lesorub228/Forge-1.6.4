// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Mod {
    String modid();
    
    String name() default "";
    
    String version() default "";
    
    String dependencies() default "";
    
    boolean useMetadata() default false;
    
    String acceptedMinecraftVersions() default "";
    
    String bukkitPlugin() default "";
    
    @Deprecated
    String modExclusionList() default "";
    
    String certificateFingerprint() default "";
    
    String modLanguage() default "java";
    
    @Deprecated
    String asmHookClass() default "";
    
    CustomProperty[] customProperties() default {};
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface InstanceFactory {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @Deprecated
    public @interface Item {
        String name();
        
        String typeClass();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @Deprecated
    public @interface Block {
        String name();
        
        Class<?> itemTypeClass() default zh.class;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    public @interface Metadata {
        String value() default "";
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    public @interface Instance {
        String value() default "";
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface IMCCallback {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface ServerStopped {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface ServerStopping {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface ServerStarted {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface ServerStarting {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface ServerAboutToStart {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface PostInit {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface Init {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface PreInit {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @Deprecated
    public @interface FingerprintWarning {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface EventHandler {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    public @interface CustomProperty {
        String k();
        
        String v();
    }
}
