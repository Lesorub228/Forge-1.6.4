// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.relauncher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.util.Map;

public interface IFMLLoadingPlugin
{
    @Deprecated
    String[] getLibraryRequestClass();
    
    String[] getASMTransformerClass();
    
    String getModContainerClass();
    
    String getSetupClass();
    
    void injectData(final Map<String, Object> p0);
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface SortingIndex {
        int value() default 0;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface DependsOn {
        String[] value() default {};
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface Name {
        String value() default "";
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface MCVersion {
        String value() default "";
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface TransformerExclusions {
        String[] value() default { "" };
    }
}
