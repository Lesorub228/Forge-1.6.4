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
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import java.net.URL;
import java.util.Iterator;
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
import com.google.common.collect.ListMultimap;
import net.minecraft.launchwrapper.IClassTransformer;

public class MarkerTransformer implements IClassTransformer
{
    private ListMultimap<String, String> markers;
    
    public MarkerTransformer() throws IOException {
        this("fml_marker.cfg");
    }
    
    protected MarkerTransformer(final String rulesFile) throws IOException {
        this.markers = (ListMultimap<String, String>)ArrayListMultimap.create();
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
                if (parts.size() != 2) {
                    throw new RuntimeException("Invalid config file line " + input);
                }
                final List<String> markerInterfaces = Lists.newArrayList(Splitter.on(",").trimResults().split((CharSequence)parts.get(1)));
                for (final String marker : markerInterfaces) {
                    MarkerTransformer.this.markers.put((Object)parts.get(0), (Object)marker);
                }
                return true;
            }
        });
    }
    
    public byte[] transform(final String name, final String transformedName, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (!this.markers.containsKey((Object)name)) {
            return bytes;
        }
        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(bytes);
        classReader.accept((ClassVisitor)classNode, 0);
        for (final String marker : this.markers.get((Object)name)) {
            classNode.interfaces.add(marker);
        }
        final ClassWriter writer = new ClassWriter(1);
        classNode.accept((ClassVisitor)writer);
        return writer.toByteArray();
    }
    
    public static void main(final String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: MarkerTransformer <JarPath> <MapFile> [MapFile2]... ");
            return;
        }
        boolean hasTransformer = false;
        final MarkerTransformer[] trans = new MarkerTransformer[args.length - 1];
        for (int x = 1; x < args.length; ++x) {
            try {
                trans[x - 1] = new MarkerTransformer(args[x]);
                hasTransformer = true;
            }
            catch (final IOException e) {
                System.out.println("Could not read Transformer Map: " + args[x]);
                e.printStackTrace();
            }
        }
        if (!hasTransformer) {
            System.out.println("Culd not find a valid transformer to perform");
            return;
        }
        final File orig = new File(args[0]);
        final File temp = new File(args[0] + ".ATBack");
        if (!orig.exists() && !temp.exists()) {
            System.out.println("Could not find target jar: " + orig);
            return;
        }
        if (!orig.renameTo(temp)) {
            System.out.println("Could not rename file: " + orig + " -> " + temp);
            return;
        }
        try {
            processJar(temp, orig, trans);
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        if (!temp.delete()) {
            System.out.println("Could not delete temp file: " + temp);
        }
    }
    
    private static void processJar(final File inFile, final File outFile, final MarkerTransformer[] transformers) throws IOException {
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
                        for (final MarkerTransformer trans : transformers) {
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
}
