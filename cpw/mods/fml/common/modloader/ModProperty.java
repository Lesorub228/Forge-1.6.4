// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import java.util.Map;
import java.lang.reflect.Field;

public class ModProperty
{
    private String info;
    private double min;
    private double max;
    private String name;
    private Field field;
    
    public ModProperty(final Field f, final String info, final Double min, final Double max, final String name) {
        this.field = f;
        this.info = info;
        this.min = ((min != null) ? min : Double.MIN_VALUE);
        this.max = ((max != null) ? max : Double.MAX_VALUE);
        this.name = name;
    }
    
    public ModProperty(final Field field, final Map<String, Object> annotationInfo) {
        this(field, annotationInfo.get("info"), annotationInfo.get("min"), annotationInfo.get("max"), annotationInfo.get("name"));
    }
    
    public String name() {
        return this.name;
    }
    
    public double min() {
        return this.min;
    }
    
    public double max() {
        return this.max;
    }
    
    public String info() {
        return this.info;
    }
    
    public Field field() {
        return this.field;
    }
}
