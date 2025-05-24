// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery.asm;

import java.util.Iterator;
import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.discovery.ASMDataTable;
import java.util.List;
import java.util.Collections;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import java.io.IOException;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.LinkedList;
import org.objectweb.asm.Type;

public class ASMModParser
{
    private Type asmType;
    private int classVersion;
    private Type asmSuperType;
    private LinkedList<ModAnnotation> annotations;
    private String baseModProperties;
    
    public ASMModParser(final InputStream stream) throws IOException {
        this.annotations = Lists.newLinkedList();
        try {
            final ClassReader reader = new ClassReader(stream);
            reader.accept((ClassVisitor)new ModClassVisitor(this), 0);
        }
        catch (final Exception ex) {
            FMLLog.log(Level.SEVERE, ex, "Unable to read a class file correctly", new Object[0]);
            throw new LoaderException(ex);
        }
    }
    
    public void beginNewTypeName(final String typeQName, final int classVersion, final String superClassQName) {
        this.asmType = Type.getObjectType(typeQName);
        this.classVersion = classVersion;
        this.asmSuperType = (Strings.isNullOrEmpty(superClassQName) ? null : Type.getObjectType(superClassQName));
    }
    
    public void startClassAnnotation(final String annotationName) {
        final ModAnnotation ann = new ModAnnotation(AnnotationType.CLASS, Type.getType(annotationName), this.asmType.getClassName());
        this.annotations.addFirst(ann);
    }
    
    public void addAnnotationProperty(final String key, final Object value) {
        this.annotations.getFirst().addProperty(key, value);
    }
    
    public void startFieldAnnotation(final String fieldName, final String annotationName) {
        final ModAnnotation ann = new ModAnnotation(AnnotationType.FIELD, Type.getType(annotationName), fieldName);
        this.annotations.addFirst(ann);
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper("ASMAnnotationDiscoverer").add("className", (Object)this.asmType.getClassName()).add("classVersion", this.classVersion).add("superName", (Object)this.asmSuperType.getClassName()).add("annotations", (Object)this.annotations).add("isBaseMod", this.isBaseMod(Collections.emptyList())).add("baseModProperties", (Object)this.baseModProperties).toString();
    }
    
    public Type getASMType() {
        return this.asmType;
    }
    
    public int getClassVersion() {
        return this.classVersion;
    }
    
    public Type getASMSuperType() {
        return this.asmSuperType;
    }
    
    public LinkedList<ModAnnotation> getAnnotations() {
        return this.annotations;
    }
    
    public void validate() {
    }
    
    public boolean isBaseMod(final List<String> rememberedTypes) {
        return this.getASMSuperType().equals((Object)Type.getType("LBaseMod;")) || this.getASMSuperType().equals((Object)Type.getType("Lnet/minecraft/src/BaseMod;")) || rememberedTypes.contains(this.getASMSuperType().getClassName());
    }
    
    public void setBaseModProperties(final String foundProperties) {
        this.baseModProperties = foundProperties;
    }
    
    public String getBaseModProperties() {
        return this.baseModProperties;
    }
    
    public void sendToTable(final ASMDataTable table, final ModCandidate candidate) {
        for (final ModAnnotation ma : this.annotations) {
            table.addASMData(candidate, ma.asmType.getClassName(), this.asmType.getClassName(), ma.member, ma.values);
        }
    }
    
    public void addAnnotationArray(final String name) {
        this.annotations.getFirst().addArray(name);
    }
    
    public void addAnnotationEnumProperty(final String name, final String desc, final String value) {
        this.annotations.getFirst().addEnumProperty(name, desc, value);
    }
    
    public void endArray() {
        this.annotations.getFirst().endArray();
    }
    
    public void addSubAnnotation(final String name, final String desc) {
        final ModAnnotation ma = this.annotations.getFirst();
        this.annotations.addFirst(ma.addChildAnnotation(name, desc));
    }
    
    public void endSubAnnotation() {
        final ModAnnotation child = this.annotations.removeFirst();
        this.annotations.addLast(child);
    }
    
    public void startMethodAnnotation(final String methodName, final String methodDescriptor, final String annotationName) {
        final ModAnnotation ann = new ModAnnotation(AnnotationType.METHOD, Type.getType(annotationName), methodName + methodDescriptor);
        this.annotations.addFirst(ann);
    }
    
    enum AnnotationType
    {
        CLASS, 
        FIELD, 
        METHOD, 
        SUBTYPE;
    }
}
