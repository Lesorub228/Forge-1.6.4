// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.transformers;

import java.util.Iterator;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.Type;
import net.minecraftforge.event.Event;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.ClassReader;
import net.minecraft.launchwrapper.IClassTransformer;

public class EventTransformer implements IClassTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        if (bytes == null || name.equals("net.minecraftforge.event.Event") || name.startsWith("net.minecraft.") || name.indexOf(46) == -1) {
            return bytes;
        }
        final ClassReader cr = new ClassReader(bytes);
        final ClassNode classNode = new ClassNode();
        cr.accept((ClassVisitor)classNode, 0);
        try {
            if (this.buildEvents(classNode)) {
                final ClassWriter cw = new ClassWriter(3);
                classNode.accept((ClassVisitor)cw);
                return cw.toByteArray();
            }
            return bytes;
        }
        catch (final ClassNotFoundException ex) {}
        catch (final Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }
    
    private boolean buildEvents(final ClassNode classNode) throws Exception {
        final Class<?> parent = this.getClass().getClassLoader().loadClass(classNode.superName.replace('/', '.'));
        if (!Event.class.isAssignableFrom(parent)) {
            return false;
        }
        boolean hasSetup = false;
        boolean hasGetListenerList = false;
        boolean hasDefaultCtr = false;
        final Class<?> listenerListClazz = Class.forName("net.minecraftforge.event.ListenerList", false, this.getClass().getClassLoader());
        final Type tList = Type.getType((Class)listenerListClazz);
        for (final MethodNode method : classNode.methods) {
            if (method.name.equals("setup") && method.desc.equals(Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])) && (method.access & 0x4) == 0x4) {
                hasSetup = true;
            }
            if (method.name.equals("getListenerList") && method.desc.equals(Type.getMethodDescriptor(tList, new Type[0])) && (method.access & 0x1) == 0x1) {
                hasGetListenerList = true;
            }
            if (method.name.equals("<init>") && method.desc.equals(Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]))) {
                hasDefaultCtr = true;
            }
        }
        if (!hasSetup) {
            final Type tSuper = Type.getType(classNode.superName);
            classNode.fields.add(new FieldNode(10, "LISTENER_LIST", tList.getDescriptor(), (String)null, (Object)null));
            MethodNode method = new MethodNode(262144, 1, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), (String)null, (String[])null);
            method.instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
            method.instructions.add((AbstractInsnNode)new MethodInsnNode(183, tSuper.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])));
            method.instructions.add((AbstractInsnNode)new InsnNode(177));
            if (!hasDefaultCtr) {
                classNode.methods.add(method);
            }
            method = new MethodNode(262144, 4, "setup", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]), (String)null, (String[])null);
            method.instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
            method.instructions.add((AbstractInsnNode)new MethodInsnNode(183, tSuper.getInternalName(), "setup", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])));
            method.instructions.add((AbstractInsnNode)new FieldInsnNode(178, classNode.name, "LISTENER_LIST", tList.getDescriptor()));
            final LabelNode initLisitener = new LabelNode();
            method.instructions.add((AbstractInsnNode)new JumpInsnNode(198, initLisitener));
            method.instructions.add((AbstractInsnNode)new InsnNode(177));
            method.instructions.add((AbstractInsnNode)initLisitener);
            method.instructions.add((AbstractInsnNode)new FrameNode(3, 0, (Object[])null, 0, (Object[])null));
            method.instructions.add((AbstractInsnNode)new TypeInsnNode(187, tList.getInternalName()));
            method.instructions.add((AbstractInsnNode)new InsnNode(89));
            method.instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
            method.instructions.add((AbstractInsnNode)new MethodInsnNode(183, tSuper.getInternalName(), "getListenerList", Type.getMethodDescriptor(tList, new Type[0])));
            method.instructions.add((AbstractInsnNode)new MethodInsnNode(183, tList.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { tList })));
            method.instructions.add((AbstractInsnNode)new FieldInsnNode(179, classNode.name, "LISTENER_LIST", tList.getDescriptor()));
            method.instructions.add((AbstractInsnNode)new InsnNode(177));
            classNode.methods.add(method);
            method = new MethodNode(262144, 1, "getListenerList", Type.getMethodDescriptor(tList, new Type[0]), (String)null, (String[])null);
            method.instructions.add((AbstractInsnNode)new FieldInsnNode(178, classNode.name, "LISTENER_LIST", tList.getDescriptor()));
            method.instructions.add((AbstractInsnNode)new InsnNode(176));
            classNode.methods.add(method);
            return true;
        }
        if (!hasGetListenerList) {
            throw new RuntimeException("Event class defines setup() but does not define getListenerList! " + classNode.name);
        }
        return false;
    }
}
