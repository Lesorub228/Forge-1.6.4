// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm.transformers;

import java.util.zip.ZipEntry;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Collection;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import java.net.URL;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.base.Splitter;
import com.google.common.io.LineProcessor;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.File;
import com.google.common.collect.ArrayListMultimap;
import java.io.IOException;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;

public class AccessTransformer implements IClassTransformer
{
    private static final boolean DEBUG = false;
    private Multimap<String, Modifier> modifiers;
    
    public AccessTransformer() throws IOException {
        this("fml_at.cfg");
    }
    
    protected AccessTransformer(final String rulesFile) throws IOException {
        this.modifiers = (Multimap<String, Modifier>)ArrayListMultimap.create();
        this.readMapFile(rulesFile);
    }
    
    private void readMapFile(final String rulesFile) throws IOException {
        final File file = new File(rulesFile);
        URL rulesResource;
        if (file.exists()) {
            rulesResource = file.toURI().toURL();
        }
        else {
            rulesResource = Resources.getResource(rulesFile);
        }
        Resources.readLines(rulesResource, Charsets.UTF_8, (LineProcessor)new LineProcessor<Void>() {
            public Void getResult() {
                return null;
            }
            
            public boolean processLine(final String input) throws IOException {
                final String line = ((String)Iterables.getFirst(Splitter.on('#').limit(2).split((CharSequence)input), (Object)"")).trim();
                if (line.length() == 0) {
                    return true;
                }
                final List<String> parts = Lists.newArrayList(Splitter.on(" ").trimResults().split((CharSequence)line));
                if (parts.size() > 2) {
                    throw new RuntimeException("Invalid config file line " + input);
                }
                final Modifier m = new Modifier();
                m.setTargetAccess(parts.get(0));
                final List<String> descriptor = Lists.newArrayList(Splitter.on(".").trimResults().split((CharSequence)parts.get(1)));
                if (descriptor.size() == 1) {
                    m.modifyClassVisibility = true;
                }
                else {
                    final String nameReference = descriptor.get(1);
                    final int parenIdx = nameReference.indexOf(40);
                    if (parenIdx > 0) {
                        m.desc = nameReference.substring(parenIdx);
                        m.name = nameReference.substring(0, parenIdx);
                    }
                    else {
                        m.name = nameReference;
                    }
                }
                AccessTransformer.this.modifiers.put((Object)descriptor.get(0).replace('/', '.'), (Object)m);
                return true;
            }
        });
        System.out.printf("Loaded %d rules from AccessTransformer config file %s\n", this.modifiers.size(), rulesFile);
    }
    
    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        final boolean makeAllPublic = FMLDeobfuscatingRemapper.INSTANCE.isRemappedClass(name);
        if (!makeAllPublic && !this.modifiers.containsKey((Object)name)) {
            return bytes;
        }
        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(bytes);
        classReader.accept((ClassVisitor)classNode, 0);
        if (makeAllPublic) {
            Modifier m = new Modifier();
            m.targetAccess = 1;
            m.modifyClassVisibility = true;
            this.modifiers.put((Object)name, (Object)m);
            m = new Modifier();
            m.targetAccess = 1;
            m.name = "*";
            this.modifiers.put((Object)name, (Object)m);
            m = new Modifier();
            m.targetAccess = 1;
            m.name = "*";
            m.desc = "<dummy>";
            this.modifiers.put((Object)name, (Object)m);
        }
        final Collection<Modifier> mods = this.modifiers.get((Object)name);
        for (final Modifier i : mods) {
            if (i.modifyClassVisibility) {
                classNode.access = this.getFixedAccess(classNode.access, i);
            }
            else if (i.desc.isEmpty()) {
                for (final FieldNode n : classNode.fields) {
                    if (n.name.equals(i.name) || i.name.equals("*")) {
                        n.access = this.getFixedAccess(n.access, i);
                        if (!i.name.equals("*")) {
                            break;
                        }
                        continue;
                    }
                }
            }
            else {
                for (final MethodNode n2 : classNode.methods) {
                    if ((n2.name.equals(i.name) && n2.desc.equals(i.desc)) || i.name.equals("*")) {
                        n2.access = this.getFixedAccess(n2.access, i);
                        if (!i.name.equals("*")) {
                            break;
                        }
                        continue;
                    }
                }
            }
        }
        final ClassWriter writer = new ClassWriter(1);
        classNode.accept((ClassVisitor)writer);
        return writer.toByteArray();
    }
    
    private String toBinary(final int num) {
        return String.format("%16s", Integer.toBinaryString(num)).replace(' ', '0');
    }
    
    private int getFixedAccess(final int access, final Modifier target) {
        target.oldAccess = access;
        final int t = target.targetAccess;
        int ret = access & 0xFFFFFFF8;
        switch (access & 0x7) {
            case 2: {
                ret |= t;
                break;
            }
            case 0: {
                ret |= ((t != 2) ? t : 0);
                break;
            }
            case 4: {
                ret |= ((t != 2 && t != 0) ? t : 4);
                break;
            }
            case 1: {
                ret |= ((t != 2 && t != 0 && t != 4) ? t : 1);
                break;
            }
            default: {
                throw new RuntimeException("The fuck?");
            }
        }
        if (target.changeFinal) {
            if (target.markFinal) {
                ret |= 0x10;
            }
            else {
                ret &= 0xFFFFFFEF;
            }
        }
        return target.newAccess = ret;
    }
    
    public static void main(final String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: AccessTransformer <JarPath> <MapFile> [MapFile2]... ");
            System.exit(1);
        }
        boolean hasTransformer = false;
        final AccessTransformer[] trans = new AccessTransformer[args.length - 1];
        for (int x = 1; x < args.length; ++x) {
            try {
                trans[x - 1] = new AccessTransformer(args[x]);
                hasTransformer = true;
            }
            catch (final IOException e) {
                System.out.println("Could not read Transformer Map: " + args[x]);
                e.printStackTrace();
            }
        }
        if (!hasTransformer) {
            System.out.println("Culd not find a valid transformer to perform");
            System.exit(1);
        }
        final File orig = new File(args[0]);
        final File temp = new File(args[0] + ".ATBack");
        if (!orig.exists() && !temp.exists()) {
            System.out.println("Could not find target jar: " + orig);
            System.exit(1);
        }
        if (!orig.renameTo(temp)) {
            System.out.println("Could not rename file: " + orig + " -> " + temp);
            System.exit(1);
        }
        try {
            processJar(temp, orig, trans);
        }
        catch (final IOException e2) {
            e2.printStackTrace();
            System.exit(1);
        }
        if (!temp.delete()) {
            System.out.println("Could not delete temp file: " + temp);
        }
    }
    
    private static void processJar(final File inFile, final File outFile, final AccessTransformer[] transformers) throws IOException {
        ZipInputStream inJar = null;
        ZipOutputStream outJar = null;
        try {
            try {
                inJar = new ZipInputStream(new BufferedInputStream(new FileInputStream(inFile)));
            }
            catch (final FileNotFoundException e) {
                throw new FileNotFoundException("Could not open input file: " + e.getMessage());
            }
            try {
                outJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            }
            catch (final FileNotFoundException e) {
                throw new FileNotFoundException("Could not open output file: " + e.getMessage());
            }
            ZipEntry entry;
            while ((entry = inJar.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    outJar.putNextEntry(entry);
                }
                else {
                    final byte[] data = new byte[4096];
                    final ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
                    int len;
                    do {
                        len = inJar.read(data);
                        if (len > 0) {
                            entryBuffer.write(data, 0, len);
                        }
                    } while (len != -1);
                    byte[] entryData = entryBuffer.toByteArray();
                    final String entryName = entry.getName();
                    if (entryName.endsWith(".class") && !entryName.startsWith(".")) {
                        final ClassNode cls = new ClassNode();
                        final ClassReader rdr = new ClassReader(entryData);
                        rdr.accept((ClassVisitor)cls, 0);
                        final String name = cls.name.replace('/', '.').replace('\\', '.');
                        for (final AccessTransformer trans : transformers) {
                            entryData = trans.transform(name, name, entryData);
                        }
                    }
                    final ZipEntry newEntry = new ZipEntry(entryName);
                    outJar.putNextEntry(newEntry);
                    outJar.write(entryData);
                }
            }
        }
        finally {
            if (outJar != null) {
                try {
                    outJar.close();
                }
                catch (final IOException ex) {}
            }
            if (inJar != null) {
                try {
                    inJar.close();
                }
                catch (final IOException ex2) {}
            }
        }
    }
    
    public void ensurePublicAccessFor(final String modClazzName) {
        final Modifier m = new Modifier();
        m.setTargetAccess("public");
        m.modifyClassVisibility = true;
        this.modifiers.put((Object)modClazzName, (Object)m);
    }
    
    private class Modifier
    {
        public String name;
        public String desc;
        public int oldAccess;
        public int newAccess;
        public int targetAccess;
        public boolean changeFinal;
        public boolean markFinal;
        protected boolean modifyClassVisibility;
        
        private Modifier() {
            this.name = "";
            this.desc = "";
            this.oldAccess = 0;
            this.newAccess = 0;
            this.targetAccess = 0;
            this.changeFinal = false;
            this.markFinal = false;
        }
        
        private void setTargetAccess(final String name) {
            if (name.startsWith("public")) {
                this.targetAccess = 1;
            }
            else if (name.startsWith("private")) {
                this.targetAccess = 2;
            }
            else if (name.startsWith("protected")) {
                this.targetAccess = 4;
            }
            if (name.endsWith("-f")) {
                this.changeFinal = true;
                this.markFinal = false;
            }
            else if (name.endsWith("+f")) {
                this.changeFinal = true;
                this.markFinal = true;
            }
        }
    }
}
