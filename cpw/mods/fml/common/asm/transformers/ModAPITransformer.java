// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm.transformers;

import java.util.Map;
import java.util.List;
import com.google.common.collect.Sets;
import java.util.Set;
import com.google.common.collect.ArrayListMultimap;
import java.util.ListIterator;
import org.objectweb.asm.tree.MethodNode;
import java.util.Iterator;
import org.objectweb.asm.ClassWriter;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import cpw.mods.fml.common.discovery.ASMDataTable;
import com.google.common.collect.ListMultimap;
import net.minecraft.launchwrapper.IClassTransformer;

public class ModAPITransformer implements IClassTransformer
{
    private static final boolean logDebugInfo;
    private ListMultimap<String, ASMDataTable.ASMData> optionals;
    
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        if (this.optionals == null || !this.optionals.containsKey((Object)name)) {
            return basicClass;
        }
        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(basicClass);
        classReader.accept((ClassVisitor)classNode, 0);
        if (ModAPITransformer.logDebugInfo) {
            FMLRelaunchLog.finest("Optional removal - found optionals for class %s - processing", name);
        }
        for (final ASMDataTable.ASMData optional : this.optionals.get((Object)name)) {
            final String modId = optional.getAnnotationInfo().get("modid");
            if (Loader.isModLoaded(modId) || ModAPIManager.INSTANCE.hasAPI(modId)) {
                if (!ModAPITransformer.logDebugInfo) {
                    continue;
                }
                FMLRelaunchLog.finest("Optional removal skipped - mod present %s", modId);
            }
            else {
                if (ModAPITransformer.logDebugInfo) {
                    FMLRelaunchLog.finest("Optional on %s triggered - mod missing %s", name, modId);
                }
                if (optional.getAnnotationInfo().containsKey("iface")) {
                    Boolean stripRefs = optional.getAnnotationInfo().get("striprefs");
                    if (stripRefs == null) {
                        stripRefs = Boolean.FALSE;
                    }
                    this.stripInterface(classNode, optional.getAnnotationInfo().get("iface"), stripRefs);
                }
                else {
                    this.stripMethod(classNode, optional.getObjectName());
                }
            }
        }
        if (ModAPITransformer.logDebugInfo) {
            FMLRelaunchLog.finest("Optional removal - class %s processed", name);
        }
        final ClassWriter writer = new ClassWriter(1);
        classNode.accept((ClassVisitor)writer);
        return writer.toByteArray();
    }
    
    private void stripMethod(final ClassNode classNode, final String methodDescriptor) {
        final ListIterator<MethodNode> iterator = classNode.methods.listIterator();
        while (iterator.hasNext()) {
            final MethodNode method = iterator.next();
            if (methodDescriptor.equals(method.name + method.desc)) {
                iterator.remove();
                if (ModAPITransformer.logDebugInfo) {
                    FMLRelaunchLog.finest("Optional removal - method %s removed", methodDescriptor);
                }
                return;
            }
        }
        if (ModAPITransformer.logDebugInfo) {
            FMLRelaunchLog.finest("Optional removal - method %s NOT removed - not found", methodDescriptor);
        }
    }
    
    private void stripInterface(final ClassNode classNode, final String interfaceName, final boolean stripRefs) {
        final String ifaceName = interfaceName.replace('.', '/');
        final boolean found = classNode.interfaces.remove(ifaceName);
        if (found && ModAPITransformer.logDebugInfo) {
            FMLRelaunchLog.finest("Optional removal - interface %s removed", interfaceName);
        }
        if (!found && ModAPITransformer.logDebugInfo) {
            FMLRelaunchLog.finest("Optional removal - interface %s NOT removed - not found", interfaceName);
        }
        if (found && stripRefs) {
            if (ModAPITransformer.logDebugInfo) {
                FMLRelaunchLog.finest("Optional removal - interface %s - stripping method signature references", interfaceName);
            }
            final Iterator<MethodNode> iterator = classNode.methods.iterator();
            while (iterator.hasNext()) {
                final MethodNode node = iterator.next();
                if (node.desc.contains(ifaceName)) {
                    if (ModAPITransformer.logDebugInfo) {
                        FMLRelaunchLog.finest("Optional removal - interface %s - stripping method containing reference %s", interfaceName, node.name);
                    }
                    iterator.remove();
                }
            }
            if (ModAPITransformer.logDebugInfo) {
                FMLRelaunchLog.finest("Optional removal - interface %s - all method signature references stripped", interfaceName);
            }
        }
        else if (found && ModAPITransformer.logDebugInfo) {
            FMLRelaunchLog.finest("Optional removal - interface %s - NOT stripping method signature references", interfaceName);
        }
    }
    
    public void initTable(final ASMDataTable dataTable) {
        this.optionals = (ListMultimap<String, ASMDataTable.ASMData>)ArrayListMultimap.create();
        final Set<ASMDataTable.ASMData> interfaceLists = dataTable.getAll("cpw.mods.fml.common.Optional$InterfaceList");
        this.addData(this.unpackInterfaces(interfaceLists));
        final Set<ASMDataTable.ASMData> interfaces = dataTable.getAll("cpw.mods.fml.common.Optional$Interface");
        this.addData(interfaces);
        final Set<ASMDataTable.ASMData> methods = dataTable.getAll("cpw.mods.fml.common.Optional$Method");
        this.addData(methods);
    }
    
    private Set<ASMDataTable.ASMData> unpackInterfaces(final Set<ASMDataTable.ASMData> packedInterfaces) {
        final Set<ASMDataTable.ASMData> result = Sets.newHashSet();
        for (final ASMDataTable.ASMData data : packedInterfaces) {
            final List<Map<String, Object>> packedList = data.getAnnotationInfo().get("value");
            for (final Map<String, Object> packed : packedList) {
                final ASMDataTable.ASMData newData = data.copy(packed);
                result.add(newData);
            }
        }
        return result;
    }
    
    private void addData(final Set<ASMDataTable.ASMData> interfaces) {
        for (final ASMDataTable.ASMData data : interfaces) {
            this.optionals.put((Object)data.getClassName(), (Object)data);
        }
    }
    
    static {
        logDebugInfo = Boolean.valueOf(System.getProperty("fml.debugAPITransformer", "true"));
    }
}
