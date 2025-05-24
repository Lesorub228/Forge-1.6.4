// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery.asm;

import com.google.common.collect.Lists;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Map;
import org.objectweb.asm.Type;

public class ModAnnotation
{
    ASMModParser.AnnotationType type;
    Type asmType;
    String member;
    Map<String, Object> values;
    private ArrayList<Object> arrayList;
    private Object array;
    private String arrayName;
    private ModAnnotation parent;
    
    public ModAnnotation(final ASMModParser.AnnotationType type, final Type asmType, final String member) {
        this.values = Maps.newHashMap();
        this.type = type;
        this.asmType = asmType;
        this.member = member;
    }
    
    public ModAnnotation(final ASMModParser.AnnotationType type, final Type asmType, final ModAnnotation parent) {
        this.values = Maps.newHashMap();
        this.type = type;
        this.asmType = asmType;
        this.parent = parent;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper("Annotation").add("type", (Object)this.type).add("name", (Object)this.asmType.getClassName()).add("member", (Object)this.member).add("values", (Object)this.values).toString();
    }
    
    public ASMModParser.AnnotationType getType() {
        return this.type;
    }
    
    public Type getASMType() {
        return this.asmType;
    }
    
    public String getMember() {
        return this.member;
    }
    
    public Map<String, Object> getValues() {
        return this.values;
    }
    
    public void addArray(final String name) {
        this.arrayList = Lists.newArrayList();
        this.arrayName = name;
    }
    
    public void addProperty(final String key, final Object value) {
        if (this.arrayList != null) {
            this.arrayList.add(value);
        }
        else {
            this.values.put(key, value);
        }
    }
    
    public void addEnumProperty(final String key, final String enumName, final String value) {
        this.values.put(key, new EnumHolder(enumName, value));
    }
    
    public void endArray() {
        this.values.put(this.arrayName, this.arrayList);
        this.arrayList = null;
    }
    
    public ModAnnotation addChildAnnotation(final String name, final String desc) {
        final ModAnnotation child = new ModAnnotation(ASMModParser.AnnotationType.SUBTYPE, Type.getType(desc), this);
        if (this.arrayList != null) {
            this.arrayList.add(child.getValues());
        }
        return child;
    }
    
    public class EnumHolder
    {
        private String desc;
        private String value;
        
        public EnumHolder(final String desc, final String value) {
            this.desc = desc;
            this.value = value;
        }
    }
}
