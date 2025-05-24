// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm.transformers.deobf;

import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Strings;
import cpw.mods.fml.common.FMLLog;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.ClassReader;
import cpw.mods.fml.common.patcher.ClassPatchManager;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import java.util.Iterator;
import java.util.List;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.collect.Iterables;
import com.google.common.base.Splitter;
import com.google.common.base.CharMatcher;
import com.google.common.io.InputSupplier;
import com.google.common.io.CharStreams;
import com.google.common.base.Charsets;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.Set;
import net.minecraft.launchwrapper.LaunchClassLoader;
import java.util.Map;
import com.google.common.collect.BiMap;
import org.objectweb.asm.commons.Remapper;

public class FMLDeobfuscatingRemapper extends Remapper
{
    public static final FMLDeobfuscatingRemapper INSTANCE;
    private BiMap<String, String> classNameBiMap;
    private BiMap<String, String> mcpNameBiMap;
    private Map<String, Map<String, String>> rawFieldMaps;
    private Map<String, Map<String, String>> rawMethodMaps;
    private Map<String, Map<String, String>> fieldNameMaps;
    private Map<String, Map<String, String>> methodNameMaps;
    private LaunchClassLoader classLoader;
    private static final boolean DEBUG_REMAPPING;
    private static final boolean DUMP_FIELD_MAPS;
    private static final boolean DUMP_METHOD_MAPS;
    private Map<String, Map<String, String>> fieldDescriptions;
    private Set<String> negativeCacheMethods;
    private Set<String> negativeCacheFields;
    
    private FMLDeobfuscatingRemapper() {
        this.fieldDescriptions = Maps.newHashMap();
        this.negativeCacheMethods = Sets.newHashSet();
        this.negativeCacheFields = Sets.newHashSet();
        this.classNameBiMap = (BiMap<String, String>)ImmutableBiMap.of();
        this.mcpNameBiMap = (BiMap<String, String>)ImmutableBiMap.of();
    }
    
    public void setupLoadOnly(final String deobfFileName, final boolean loadAll) {
        try {
            final File mapData = new File(deobfFileName);
            final LZMAInputSupplier zis = new LZMAInputSupplier(new FileInputStream(mapData));
            final InputSupplier<InputStreamReader> srgSupplier = (InputSupplier<InputStreamReader>)CharStreams.newReaderSupplier((InputSupplier)zis, Charsets.UTF_8);
            final List<String> srgList = CharStreams.readLines((InputSupplier)srgSupplier);
            this.rawMethodMaps = Maps.newHashMap();
            this.rawFieldMaps = Maps.newHashMap();
            final ImmutableBiMap.Builder<String, String> builder = (ImmutableBiMap.Builder<String, String>)ImmutableBiMap.builder();
            final ImmutableBiMap.Builder<String, String> mcpBuilder = (ImmutableBiMap.Builder<String, String>)ImmutableBiMap.builder();
            final Splitter splitter = Splitter.on(CharMatcher.anyOf((CharSequence)": ")).omitEmptyStrings().trimResults();
            for (final String line : srgList) {
                final String[] parts = (String[])Iterables.toArray(splitter.split((CharSequence)line), (Class)String.class);
                final String typ = parts[0];
                if ("CL".equals(typ)) {
                    this.parseClass(builder, parts);
                    this.parseMCPClass(mcpBuilder, parts);
                }
                else if ("MD".equals(typ) && loadAll) {
                    this.parseMethod(parts);
                }
                else {
                    if (!"FD".equals(typ) || !loadAll) {
                        continue;
                    }
                    this.parseField(parts);
                }
            }
            this.classNameBiMap = (BiMap<String, String>)builder.build();
            mcpBuilder.put((Object)"BaseMod", (Object)"net/minecraft/src/BaseMod");
            mcpBuilder.put((Object)"ModLoader", (Object)"net/minecraft/src/ModLoader");
            mcpBuilder.put((Object)"EntityRendererProxy", (Object)"net/minecraft/src/EntityRendererProxy");
            mcpBuilder.put((Object)"MLProp", (Object)"net/minecraft/src/MLProp");
            mcpBuilder.put((Object)"TradeEntry", (Object)"net/minecraft/src/TradeEntry");
            this.mcpNameBiMap = (BiMap<String, String>)mcpBuilder.build();
        }
        catch (final IOException ioe) {
            Logger.getLogger("FML").log(Level.SEVERE, "An error occurred loading the deobfuscation map data", ioe);
        }
        this.methodNameMaps = Maps.newHashMapWithExpectedSize(this.rawMethodMaps.size());
        this.fieldNameMaps = Maps.newHashMapWithExpectedSize(this.rawFieldMaps.size());
    }
    
    public void setup(final File mcDir, final LaunchClassLoader classLoader, final String deobfFileName) {
        this.classLoader = classLoader;
        try {
            final InputStream classData = this.getClass().getResourceAsStream(deobfFileName);
            final LZMAInputSupplier zis = new LZMAInputSupplier(classData);
            final InputSupplier<InputStreamReader> srgSupplier = (InputSupplier<InputStreamReader>)CharStreams.newReaderSupplier((InputSupplier)zis, Charsets.UTF_8);
            final List<String> srgList = CharStreams.readLines((InputSupplier)srgSupplier);
            this.rawMethodMaps = Maps.newHashMap();
            this.rawFieldMaps = Maps.newHashMap();
            final ImmutableBiMap.Builder<String, String> builder = (ImmutableBiMap.Builder<String, String>)ImmutableBiMap.builder();
            final ImmutableBiMap.Builder<String, String> mcpBuilder = (ImmutableBiMap.Builder<String, String>)ImmutableBiMap.builder();
            final Splitter splitter = Splitter.on(CharMatcher.anyOf((CharSequence)": ")).omitEmptyStrings().trimResults();
            for (final String line : srgList) {
                final String[] parts = (String[])Iterables.toArray(splitter.split((CharSequence)line), (Class)String.class);
                final String typ = parts[0];
                if ("CL".equals(typ)) {
                    this.parseClass(builder, parts);
                    this.parseMCPClass(mcpBuilder, parts);
                }
                else if ("MD".equals(typ)) {
                    this.parseMethod(parts);
                }
                else {
                    if (!"FD".equals(typ)) {
                        continue;
                    }
                    this.parseField(parts);
                }
            }
            this.classNameBiMap = (BiMap<String, String>)builder.build();
            mcpBuilder.put((Object)"BaseMod", (Object)"net/minecraft/src/BaseMod");
            mcpBuilder.put((Object)"ModLoader", (Object)"net/minecraft/src/ModLoader");
            mcpBuilder.put((Object)"EntityRendererProxy", (Object)"net/minecraft/src/EntityRendererProxy");
            mcpBuilder.put((Object)"MLProp", (Object)"net/minecraft/src/MLProp");
            mcpBuilder.put((Object)"TradeEntry", (Object)"net/minecraft/src/TradeEntry");
            this.mcpNameBiMap = (BiMap<String, String>)mcpBuilder.build();
        }
        catch (final IOException ioe) {
            FMLRelaunchLog.log(Level.SEVERE, ioe, "An error occurred loading the deobfuscation map data", new Object[0]);
        }
        this.methodNameMaps = Maps.newHashMapWithExpectedSize(this.rawMethodMaps.size());
        this.fieldNameMaps = Maps.newHashMapWithExpectedSize(this.rawFieldMaps.size());
    }
    
    public boolean isRemappedClass(String className) {
        className = className.replace('.', '/');
        return this.classNameBiMap.containsKey((Object)className) || this.mcpNameBiMap.containsKey((Object)className) || (!this.classNameBiMap.isEmpty() && className.indexOf(47) == -1);
    }
    
    private void parseField(final String[] parts) {
        final String oldSrg = parts[1];
        final int lastOld = oldSrg.lastIndexOf(47);
        final String cl = oldSrg.substring(0, lastOld);
        final String oldName = oldSrg.substring(lastOld + 1);
        final String newSrg = parts[2];
        final int lastNew = newSrg.lastIndexOf(47);
        final String newName = newSrg.substring(lastNew + 1);
        if (!this.rawFieldMaps.containsKey(cl)) {
            this.rawFieldMaps.put(cl, Maps.newHashMap());
        }
        this.rawFieldMaps.get(cl).put(oldName + ":" + this.getFieldType(cl, oldName), newName);
        this.rawFieldMaps.get(cl).put(oldName + ":null", newName);
    }
    
    private String getFieldType(final String owner, final String name) {
        if (this.fieldDescriptions.containsKey(owner)) {
            return (String)this.fieldDescriptions.get(owner).get(name);
        }
        synchronized (this.fieldDescriptions) {
            try {
                final byte[] classBytes = ClassPatchManager.INSTANCE.getPatchedResource(owner, this.map(owner).replace('/', '.'), this.classLoader);
                if (classBytes == null) {
                    return null;
                }
                final ClassReader cr = new ClassReader(classBytes);
                final ClassNode classNode = new ClassNode();
                cr.accept((ClassVisitor)classNode, 7);
                final Map<String, String> resMap = Maps.newHashMap();
                for (final FieldNode fieldNode : classNode.fields) {
                    resMap.put(fieldNode.name, fieldNode.desc);
                }
                this.fieldDescriptions.put(owner, resMap);
                return resMap.get(name);
            }
            catch (final IOException e) {
                FMLLog.log(Level.SEVERE, e, "A critical exception occured reading a class file %s", owner);
                return null;
            }
        }
    }
    
    private void parseClass(final ImmutableBiMap.Builder<String, String> builder, final String[] parts) {
        builder.put((Object)parts[1], (Object)parts[2]);
    }
    
    private void parseMCPClass(final ImmutableBiMap.Builder<String, String> builder, final String[] parts) {
        final int clIdx = parts[2].lastIndexOf(47);
        builder.put((Object)("net/minecraft/src/" + parts[2].substring(clIdx + 1)), (Object)parts[2]);
    }
    
    private void parseMethod(final String[] parts) {
        final String oldSrg = parts[1];
        final int lastOld = oldSrg.lastIndexOf(47);
        final String cl = oldSrg.substring(0, lastOld);
        final String oldName = oldSrg.substring(lastOld + 1);
        final String sig = parts[2];
        final String newSrg = parts[3];
        final int lastNew = newSrg.lastIndexOf(47);
        final String newName = newSrg.substring(lastNew + 1);
        if (!this.rawMethodMaps.containsKey(cl)) {
            this.rawMethodMaps.put(cl, Maps.newHashMap());
        }
        this.rawMethodMaps.get(cl).put(oldName + sig, newName);
    }
    
    public String mapFieldName(final String owner, final String name, final String desc) {
        if (this.classNameBiMap == null || this.classNameBiMap.isEmpty()) {
            return name;
        }
        final Map<String, String> fieldMap = this.getFieldMap(owner);
        return (fieldMap != null && fieldMap.containsKey(name + ":" + desc)) ? fieldMap.get(name + ":" + desc) : name;
    }
    
    public String map(final String typeName) {
        if (this.classNameBiMap == null || this.classNameBiMap.isEmpty()) {
            return typeName;
        }
        final int dollarIdx = typeName.indexOf(36);
        final String realType = (dollarIdx > -1) ? typeName.substring(0, dollarIdx) : typeName;
        final String subType = (dollarIdx > -1) ? typeName.substring(dollarIdx + 1) : "";
        String result = (String)(this.classNameBiMap.containsKey((Object)realType) ? this.classNameBiMap.get((Object)realType) : (this.mcpNameBiMap.containsKey((Object)realType) ? this.mcpNameBiMap.get((Object)realType) : realType));
        result = ((dollarIdx > -1) ? (result + "$" + subType) : result);
        return result;
    }
    
    public String unmap(final String typeName) {
        if (this.classNameBiMap == null || this.classNameBiMap.isEmpty()) {
            return typeName;
        }
        final int dollarIdx = typeName.indexOf(36);
        final String realType = (dollarIdx > -1) ? typeName.substring(0, dollarIdx) : typeName;
        final String subType = (dollarIdx > -1) ? typeName.substring(dollarIdx + 1) : "";
        String result = (String)(this.classNameBiMap.containsValue((Object)realType) ? this.classNameBiMap.inverse().get((Object)realType) : (this.mcpNameBiMap.containsValue((Object)realType) ? this.mcpNameBiMap.inverse().get((Object)realType) : realType));
        result = ((dollarIdx > -1) ? (result + "$" + subType) : result);
        return result;
    }
    
    public String mapMethodName(final String owner, final String name, final String desc) {
        if (this.classNameBiMap == null || this.classNameBiMap.isEmpty()) {
            return name;
        }
        final Map<String, String> methodMap = this.getMethodMap(owner);
        final String methodDescriptor = name + desc;
        return (methodMap != null && methodMap.containsKey(methodDescriptor)) ? methodMap.get(methodDescriptor) : name;
    }
    
    private Map<String, String> getFieldMap(final String className) {
        if (!this.fieldNameMaps.containsKey(className) && !this.negativeCacheFields.contains(className)) {
            this.findAndMergeSuperMaps(className);
            if (!this.fieldNameMaps.containsKey(className)) {
                this.negativeCacheFields.add(className);
            }
            if (FMLDeobfuscatingRemapper.DUMP_FIELD_MAPS) {
                FMLRelaunchLog.finest("Field map for %s : %s", className, this.fieldNameMaps.get(className));
            }
        }
        return this.fieldNameMaps.get(className);
    }
    
    private Map<String, String> getMethodMap(final String className) {
        if (!this.methodNameMaps.containsKey(className) && !this.negativeCacheMethods.contains(className)) {
            this.findAndMergeSuperMaps(className);
            if (!this.methodNameMaps.containsKey(className)) {
                this.negativeCacheMethods.add(className);
            }
            if (FMLDeobfuscatingRemapper.DUMP_METHOD_MAPS) {
                FMLRelaunchLog.finest("Method map for %s : %s", className, this.methodNameMaps.get(className));
            }
        }
        return this.methodNameMaps.get(className);
    }
    
    private void findAndMergeSuperMaps(final String name) {
        try {
            String superName = null;
            String[] interfaces = new String[0];
            final byte[] classBytes = ClassPatchManager.INSTANCE.getPatchedResource(name, this.map(name), this.classLoader);
            if (classBytes != null) {
                final ClassReader cr = new ClassReader(classBytes);
                superName = cr.getSuperName();
                interfaces = cr.getInterfaces();
            }
            this.mergeSuperMaps(name, superName, interfaces);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    public void mergeSuperMaps(final String name, final String superName, final String[] interfaces) {
        if (this.classNameBiMap == null || this.classNameBiMap.isEmpty()) {
            return;
        }
        if (Strings.isNullOrEmpty(superName)) {
            return;
        }
        final List<String> allParents = (List<String>)ImmutableList.builder().add((Object)superName).addAll((Iterable)Arrays.asList(interfaces)).build();
        for (final String parentThing : allParents) {
            if (!this.methodNameMaps.containsKey(parentThing)) {
                this.findAndMergeSuperMaps(parentThing);
            }
        }
        final Map<String, String> methodMap = Maps.newHashMap();
        final Map<String, String> fieldMap = Maps.newHashMap();
        for (final String parentThing2 : allParents) {
            if (this.methodNameMaps.containsKey(parentThing2)) {
                methodMap.putAll(this.methodNameMaps.get(parentThing2));
            }
            if (this.fieldNameMaps.containsKey(parentThing2)) {
                fieldMap.putAll(this.fieldNameMaps.get(parentThing2));
            }
        }
        if (this.rawMethodMaps.containsKey(name)) {
            methodMap.putAll(this.rawMethodMaps.get(name));
        }
        if (this.rawFieldMaps.containsKey(name)) {
            fieldMap.putAll(this.rawFieldMaps.get(name));
        }
        this.methodNameMaps.put(name, (Map<String, String>)ImmutableMap.copyOf((Map)methodMap));
        this.fieldNameMaps.put(name, (Map<String, String>)ImmutableMap.copyOf((Map)fieldMap));
    }
    
    public Set<String> getObfedClasses() {
        return (Set<String>)ImmutableSet.copyOf((Collection)this.classNameBiMap.keySet());
    }
    
    static {
        INSTANCE = new FMLDeobfuscatingRemapper();
        DEBUG_REMAPPING = Boolean.parseBoolean(System.getProperty("fml.remappingDebug", "false"));
        DUMP_FIELD_MAPS = (Boolean.parseBoolean(System.getProperty("fml.remappingDebug.dumpFieldMaps", "false")) && FMLDeobfuscatingRemapper.DEBUG_REMAPPING);
        DUMP_METHOD_MAPS = (Boolean.parseBoolean(System.getProperty("fml.remappingDebug.dumpMethodMaps", "false")) && FMLDeobfuscatingRemapper.DEBUG_REMAPPING);
    }
}
