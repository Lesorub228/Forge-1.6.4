// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.asm.transformers;

import com.google.common.base.Objects;
import java.util.LinkedHashSet;
import com.google.common.collect.Lists;
import org.objectweb.asm.tree.MethodNode;
import com.google.common.collect.Sets;
import java.util.List;
import org.objectweb.asm.tree.FieldNode;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.ClassWriter;
import java.util.ArrayList;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.ClassReader;
import java.util.Iterator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.zip.ZipEntry;
import java.util.Map;
import java.util.zip.ZipOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipFile;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;

public class MCPMerger
{
    private static Hashtable<String, ClassInfo> clients;
    private static Hashtable<String, ClassInfo> shared;
    private static Hashtable<String, ClassInfo> servers;
    private static HashSet<String> copyToServer;
    private static HashSet<String> copyToClient;
    private static HashSet<String> dontAnnotate;
    private static HashSet<String> dontProcess;
    private static final boolean DEBUG = false;
    
    public static void main(final String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: MCPMerger <MapFile> <minecraft.jar> <minecraft_server.jar>");
            System.exit(1);
        }
        final File map_file = new File(args[0]);
        final File client_jar = new File(args[1]);
        final File server_jar = new File(args[2]);
        final File client_jar_tmp = new File(args[1] + ".backup_merge");
        final File server_jar_tmp = new File(args[2] + ".backup_merge");
        if (client_jar_tmp.exists() && !client_jar_tmp.delete()) {
            System.out.println("Could not delete temp file: " + client_jar_tmp);
        }
        if (server_jar_tmp.exists() && !server_jar_tmp.delete()) {
            System.out.println("Could not delete temp file: " + server_jar_tmp);
        }
        if (!client_jar.exists()) {
            System.out.println("Could not find minecraft.jar: " + client_jar);
            System.exit(1);
        }
        if (!server_jar.exists()) {
            System.out.println("Could not find minecraft_server.jar: " + server_jar);
            System.exit(1);
        }
        if (!client_jar.renameTo(client_jar_tmp)) {
            System.out.println("Could not rename file: " + client_jar + " -> " + client_jar_tmp);
            System.exit(1);
        }
        if (!server_jar.renameTo(server_jar_tmp)) {
            System.out.println("Could not rename file: " + server_jar + " -> " + server_jar_tmp);
            System.exit(1);
        }
        if (!readMapFile(map_file)) {
            System.out.println("Could not read map file: " + map_file);
            System.exit(1);
        }
        try {
            processJar(client_jar_tmp, server_jar_tmp, client_jar, server_jar);
        }
        catch (final IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (!client_jar_tmp.delete()) {
            System.out.println("Could not delete temp file: " + client_jar_tmp);
        }
        if (!server_jar_tmp.delete()) {
            System.out.println("Could not delete temp file: " + server_jar_tmp);
        }
    }
    
    private static boolean readMapFile(final File mapFile) {
        try {
            final FileInputStream fstream = new FileInputStream(mapFile);
            final DataInputStream in = new DataInputStream(fstream);
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.split("#")[0];
                final char cmd = line.charAt(0);
                line = line.substring(1).trim();
                switch (cmd) {
                    case '!': {
                        MCPMerger.dontAnnotate.add(line);
                        continue;
                    }
                    case '<': {
                        MCPMerger.copyToClient.add(line);
                        continue;
                    }
                    case '>': {
                        MCPMerger.copyToServer.add(line);
                        continue;
                    }
                    case '^': {
                        MCPMerger.dontProcess.add(line);
                        continue;
                    }
                }
            }
            in.close();
            return true;
        }
        catch (final Exception e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    public static void processJar(final File clientInFile, final File serverInFile, final File clientOutFile, final File serverOutFile) throws IOException {
        ZipFile cInJar = null;
        ZipFile sInJar = null;
        ZipOutputStream cOutJar = null;
        ZipOutputStream sOutJar = null;
        try {
            try {
                cInJar = new ZipFile(clientInFile);
                sInJar = new ZipFile(serverInFile);
            }
            catch (final FileNotFoundException e) {
                throw new FileNotFoundException("Could not open input file: " + e.getMessage());
            }
            try {
                cOutJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(clientOutFile)));
                sOutJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(serverOutFile)));
            }
            catch (final FileNotFoundException e) {
                throw new FileNotFoundException("Could not open output file: " + e.getMessage());
            }
            final Hashtable<String, ZipEntry> cClasses = getClassEntries(cInJar, cOutJar);
            final Hashtable<String, ZipEntry> sClasses = getClassEntries(sInJar, sOutJar);
            final HashSet<String> cAdded = new HashSet<String>();
            final HashSet<String> sAdded = new HashSet<String>();
            for (final Map.Entry<String, ZipEntry> entry : cClasses.entrySet()) {
                final String name = entry.getKey();
                final ZipEntry cEntry = entry.getValue();
                final ZipEntry sEntry = sClasses.get(name);
                if (sEntry == null) {
                    if (!MCPMerger.copyToServer.contains(name)) {
                        copyClass(cInJar, cEntry, cOutJar, null, true);
                        cAdded.add(name);
                    }
                    else {
                        copyClass(cInJar, cEntry, cOutJar, sOutJar, true);
                        cAdded.add(name);
                        sAdded.add(name);
                    }
                }
                else {
                    sClasses.remove(name);
                    final ClassInfo info = new ClassInfo(name);
                    MCPMerger.shared.put(name, info);
                    final byte[] cData = readEntry(cInJar, entry.getValue());
                    final byte[] sData = readEntry(sInJar, sEntry);
                    final byte[] data = processClass(cData, sData, info);
                    final ZipEntry newEntry = new ZipEntry(cEntry.getName());
                    cOutJar.putNextEntry(newEntry);
                    cOutJar.write(data);
                    sOutJar.putNextEntry(newEntry);
                    sOutJar.write(data);
                    cAdded.add(name);
                    sAdded.add(name);
                }
            }
            for (final Map.Entry<String, ZipEntry> entry : sClasses.entrySet()) {
                copyClass(sInJar, entry.getValue(), cOutJar, sOutJar, false);
            }
            for (final String name2 : new String[] { SideOnly.class.getName(), Side.class.getName() }) {
                final String eName = name2.replace(".", "/");
                final byte[] data2 = getClassBytes(name2);
                final ZipEntry newEntry2 = new ZipEntry(name2.replace(".", "/").concat(".class"));
                if (!cAdded.contains(eName)) {
                    cOutJar.putNextEntry(newEntry2);
                    cOutJar.write(data2);
                }
                if (!sAdded.contains(eName)) {
                    sOutJar.putNextEntry(newEntry2);
                    sOutJar.write(data2);
                }
            }
        }
        finally {
            if (cInJar != null) {
                try {
                    cInJar.close();
                }
                catch (final IOException ex) {}
            }
            if (sInJar != null) {
                try {
                    sInJar.close();
                }
                catch (final IOException ex2) {}
            }
            if (cOutJar != null) {
                try {
                    cOutJar.close();
                }
                catch (final IOException ex3) {}
            }
            if (sOutJar != null) {
                try {
                    sOutJar.close();
                }
                catch (final IOException ex4) {}
            }
        }
    }
    
    private static void copyClass(final ZipFile inJar, final ZipEntry entry, final ZipOutputStream outJar, final ZipOutputStream outJar2, final boolean isClientOnly) throws IOException {
        final ClassReader reader = new ClassReader(readEntry(inJar, entry));
        final ClassNode classNode = new ClassNode();
        reader.accept((ClassVisitor)classNode, 0);
        if (!MCPMerger.dontAnnotate.contains(classNode.name)) {
            if (classNode.visibleAnnotations == null) {
                classNode.visibleAnnotations = new ArrayList();
            }
            classNode.visibleAnnotations.add(getSideAnn(isClientOnly));
        }
        final ClassWriter writer = new ClassWriter(1);
        classNode.accept((ClassVisitor)writer);
        final byte[] data = writer.toByteArray();
        final ZipEntry newEntry = new ZipEntry(entry.getName());
        if (outJar != null) {
            outJar.putNextEntry(newEntry);
            outJar.write(data);
        }
        if (outJar2 != null) {
            outJar2.putNextEntry(newEntry);
            outJar2.write(data);
        }
    }
    
    private static AnnotationNode getSideAnn(final boolean isClientOnly) {
        final AnnotationNode ann = new AnnotationNode(Type.getDescriptor((Class)SideOnly.class));
        (ann.values = new ArrayList()).add("value");
        ann.values.add(new String[] { Type.getDescriptor((Class)Side.class), isClientOnly ? "CLIENT" : "SERVER" });
        return ann;
    }
    
    private static Hashtable<String, ZipEntry> getClassEntries(final ZipFile inFile, final ZipOutputStream outFile) throws IOException {
        final Hashtable<String, ZipEntry> ret = new Hashtable<String, ZipEntry>();
        for (final ZipEntry entry : Collections.list(inFile.entries())) {
            if (entry.isDirectory()) {
                outFile.putNextEntry(entry);
            }
            else {
                final String entryName = entry.getName();
                boolean filtered = false;
                for (final String filter : MCPMerger.dontProcess) {
                    if (entryName.startsWith(filter)) {
                        filtered = true;
                        break;
                    }
                }
                if (filtered || !entryName.endsWith(".class") || entryName.startsWith(".")) {
                    final ZipEntry newEntry = new ZipEntry(entry.getName());
                    outFile.putNextEntry(newEntry);
                    outFile.write(readEntry(inFile, entry));
                }
                else {
                    ret.put(entryName.replace(".class", ""), entry);
                }
            }
        }
        return ret;
    }
    
    private static byte[] readEntry(final ZipFile inFile, final ZipEntry entry) throws IOException {
        return readFully(inFile.getInputStream(entry));
    }
    
    private static byte[] readFully(final InputStream stream) throws IOException {
        final byte[] data = new byte[4096];
        final ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
        int len;
        do {
            len = stream.read(data);
            if (len > 0) {
                entryBuffer.write(data, 0, len);
            }
        } while (len != -1);
        return entryBuffer.toByteArray();
    }
    
    public static byte[] processClass(final byte[] cIn, final byte[] sIn, final ClassInfo info) {
        final ClassNode cClassNode = getClassNode(cIn);
        final ClassNode sClassNode = getClassNode(sIn);
        processFields(cClassNode, sClassNode, info);
        processMethods(cClassNode, sClassNode, info);
        final ClassWriter writer = new ClassWriter(1);
        cClassNode.accept((ClassVisitor)writer);
        return writer.toByteArray();
    }
    
    private static ClassNode getClassNode(final byte[] data) {
        final ClassReader reader = new ClassReader(data);
        final ClassNode classNode = new ClassNode();
        reader.accept((ClassVisitor)classNode, 0);
        return classNode;
    }
    
    private static void processFields(final ClassNode cClass, final ClassNode sClass, final ClassInfo info) {
        final List<FieldNode> cFields = cClass.fields;
        final List<FieldNode> sFields = sClass.fields;
        int sI = 0;
        for (int x = 0; x < cFields.size(); ++x) {
            final FieldNode cF = cFields.get(x);
            if (sI < sFields.size()) {
                if (!cF.name.equals(sFields.get(sI).name)) {
                    boolean serverHas = false;
                    for (int y = sI + 1; y < sFields.size(); ++y) {
                        if (cF.name.equals(sFields.get(y).name)) {
                            serverHas = true;
                            break;
                        }
                    }
                    if (serverHas) {
                        boolean clientHas = false;
                        final FieldNode sF = sFields.get(sI);
                        for (int y2 = x + 1; y2 < cFields.size(); ++y2) {
                            if (sF.name.equals(cFields.get(y2).name)) {
                                clientHas = true;
                                break;
                            }
                        }
                        if (!clientHas) {
                            if (sF.visibleAnnotations == null) {
                                sF.visibleAnnotations = new ArrayList();
                            }
                            sF.visibleAnnotations.add(getSideAnn(false));
                            cFields.add(x++, sF);
                            info.sField.add(sF);
                        }
                    }
                    else {
                        if (cF.visibleAnnotations == null) {
                            cF.visibleAnnotations = new ArrayList();
                        }
                        cF.visibleAnnotations.add(getSideAnn(true));
                        sFields.add(sI, cF);
                        info.cField.add(cF);
                    }
                }
            }
            else {
                if (cF.visibleAnnotations == null) {
                    cF.visibleAnnotations = new ArrayList();
                }
                cF.visibleAnnotations.add(getSideAnn(true));
                sFields.add(sI, cF);
                info.cField.add(cF);
            }
            ++sI;
        }
        if (sFields.size() != cFields.size()) {
            for (int x = cFields.size(); x < sFields.size(); ++x) {
                final FieldNode sF2 = sFields.get(x);
                if (sF2.visibleAnnotations == null) {
                    sF2.visibleAnnotations = new ArrayList();
                }
                sF2.visibleAnnotations.add(getSideAnn(true));
                cFields.add(x++, sF2);
                info.sField.add(sF2);
            }
        }
    }
    
    private static void processMethods(final ClassNode cClass, final ClassNode sClass, final ClassInfo info) {
        final List<MethodNode> cMethods = cClass.methods;
        final List<MethodNode> sMethods = sClass.methods;
        final LinkedHashSet<MethodWrapper> allMethods = Sets.newLinkedHashSet();
        int cPos = 0;
        int sPos = 0;
        final int cLen = cMethods.size();
        final int sLen = sMethods.size();
        String lastName;
        String clientName = lastName = "";
        String serverName = "";
    Label_0153_Outer:
        while (cPos < cLen || sPos < sLen) {
            while (true) {
                while (sPos < sLen) {
                    final MethodNode sM = sMethods.get(sPos);
                    serverName = sM.name;
                    if (serverName.equals(lastName) || cPos == cLen) {
                        final MethodWrapper mw = new MethodWrapper(sM);
                        mw.server = true;
                        allMethods.add(mw);
                        if (++sPos < sLen) {
                            continue Label_0153_Outer;
                        }
                    }
                    while (cPos < cLen) {
                        final MethodNode cM = cMethods.get(cPos);
                        lastName = clientName;
                        clientName = cM.name;
                        if (!clientName.equals(lastName) && sPos != sLen) {
                            break;
                        }
                        final MethodWrapper mw = new MethodWrapper(cM);
                        mw.client = true;
                        allMethods.add(mw);
                        if (++cPos >= cLen) {
                            break;
                        }
                    }
                    continue Label_0153_Outer;
                }
                continue;
            }
        }
        cMethods.clear();
        sMethods.clear();
        final Iterator i$ = allMethods.iterator();
        while (i$.hasNext()) {
            final MethodWrapper mw = i$.next();
            cMethods.add(mw.node);
            sMethods.add(mw.node);
            if (mw.server && mw.client) {
                continue;
            }
            if (mw.node.visibleAnnotations == null) {
                mw.node.visibleAnnotations = Lists.newArrayListWithExpectedSize(1);
            }
            mw.node.visibleAnnotations.add(getSideAnn(mw.client));
            if (mw.client) {
                info.sMethods.add(mw.node);
            }
            else {
                info.cMethods.add(mw.node);
            }
        }
    }
    
    public static byte[] getClassBytes(final String name) throws IOException {
        InputStream classStream = null;
        try {
            classStream = MCPMerger.class.getResourceAsStream("/" + name.replace('.', '/').concat(".class"));
            return readFully(classStream);
        }
        finally {
            if (classStream != null) {
                try {
                    classStream.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    static {
        MCPMerger.clients = new Hashtable<String, ClassInfo>();
        MCPMerger.shared = new Hashtable<String, ClassInfo>();
        MCPMerger.servers = new Hashtable<String, ClassInfo>();
        MCPMerger.copyToServer = new HashSet<String>();
        MCPMerger.copyToClient = new HashSet<String>();
        MCPMerger.dontAnnotate = new HashSet<String>();
        MCPMerger.dontProcess = new HashSet<String>();
    }
    
    private static class ClassInfo
    {
        public String name;
        public ArrayList<FieldNode> cField;
        public ArrayList<FieldNode> sField;
        public ArrayList<MethodNode> cMethods;
        public ArrayList<MethodNode> sMethods;
        
        public ClassInfo(final String name) {
            this.cField = new ArrayList<FieldNode>();
            this.sField = new ArrayList<FieldNode>();
            this.cMethods = new ArrayList<MethodNode>();
            this.sMethods = new ArrayList<MethodNode>();
            this.name = name;
        }
        
        public boolean isSame() {
            return this.cField.size() == 0 && this.sField.size() == 0 && this.cMethods.size() == 0 && this.sMethods.size() == 0;
        }
    }
    
    private static class MethodWrapper
    {
        private MethodNode node;
        public boolean client;
        public boolean server;
        
        public MethodWrapper(final MethodNode node) {
            this.node = node;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null || !(obj instanceof MethodWrapper)) {
                return false;
            }
            final MethodWrapper mw = (MethodWrapper)obj;
            final boolean eq = Objects.equal((Object)this.node.name, (Object)mw.node.name) && Objects.equal((Object)this.node.desc, (Object)mw.node.desc);
            if (eq) {
                mw.client |= this.client;
                mw.server |= this.server;
                this.client |= mw.client;
                this.server |= mw.server;
            }
            return eq;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(new Object[] { this.node.name, this.node.desc });
        }
        
        @Override
        public String toString() {
            return Objects.toStringHelper((Object)this).add("name", (Object)this.node.name).add("desc", (Object)this.node.desc).add("server", this.server).add("client", this.client).toString();
        }
    }
}
