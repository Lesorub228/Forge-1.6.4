import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

// 
// Decompiled by Procyon v0.6.0
// 

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Deprecated
public @interface MLProp {
    @Deprecated
    String info() default "";
    
    @Deprecated
    double max() default Double.MAX_VALUE;
    
    @Deprecated
    double min() default Double.MIN_VALUE;
    
    @Deprecated
    String name() default "";
}
