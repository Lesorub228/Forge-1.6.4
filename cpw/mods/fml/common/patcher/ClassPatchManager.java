// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.patcher;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.util.jar.JarEntry;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.base.Throwables;
import java.util.jar.JarInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.jar.Pack200;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.io.ByteArrayOutputStream;
import LZMA.LzmaInputStream;
import java.util.regex.Pattern;
import java.util.Locale;
import cpw.mods.fml.relauncher.Side;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.google.common.hash.Hashing;
import java.io.IOException;
import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import com.google.common.io.Files;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import com.google.common.collect.ListMultimap;
import cpw.mods.fml.repackage.com.nothome.delta.GDiffPatcher;

public class ClassPatchManager
{
    public static final ClassPatchManager INSTANCE;
    public static final boolean dumpPatched;
    private GDiffPatcher patcher;
    private ListMultimap<String, ClassPatch> patches;
    private Map<String, byte[]> patchedClasses;
    private File tempDir;
    
    private ClassPatchManager() {
        this.patcher = new GDiffPatcher();
        this.patchedClasses = Maps.newHashMap();
        if (ClassPatchManager.dumpPatched) {
            this.tempDir = Files.createTempDir();
            FMLRelaunchLog.info("Dumping patched classes to %s", this.tempDir.getAbsolutePath());
        }
    }
    
    public byte[] getPatchedResource(final String name, final String mappedName, final LaunchClassLoader loader) throws IOException {
        final byte[] rawClassBytes = loader.getClassBytes(name);
        return this.applyPatch(name, mappedName, rawClassBytes);
    }
    
    public byte[] applyPatch(final String name, final String mappedName, byte[] inputData) {
        if (this.patches == null) {
            return inputData;
        }
        if (this.patchedClasses.containsKey(name)) {
            return this.patchedClasses.get(name);
        }
        final List<ClassPatch> list = this.patches.get((Object)name);
        if (list.isEmpty()) {
            return inputData;
        }
        boolean ignoredError = false;
        FMLRelaunchLog.fine("Runtime patching class %s (input size %d), found %d patch%s", mappedName, (inputData == null) ? 0 : inputData.length, list.size(), (list.size() != 1) ? "es" : "");
        for (final ClassPatch patch : list) {
            if (!patch.targetClassName.equals(mappedName) && !patch.sourceClassName.equals(name)) {
                FMLRelaunchLog.warning("Binary patch found %s for wrong class %s", patch.targetClassName, mappedName);
            }
            if (!patch.existsAtTarget && (inputData == null || inputData.length == 0)) {
                inputData = new byte[0];
            }
            else if (!patch.existsAtTarget) {
                FMLRelaunchLog.warning("Patcher expecting empty class data file for %s, but received non-empty", patch.targetClassName);
            }
            else {
                final int inputChecksum = Hashing.adler32().hashBytes(inputData).asInt();
                if (patch.inputChecksum != inputChecksum) {
                    FMLRelaunchLog.severe("There is a binary discrepency between the expected input class %s (%s) and the actual class. Checksum on disk is %x, in patch %x. Things are probably about to go very wrong. Did you put something into the jar file?", mappedName, name, inputChecksum, patch.inputChecksum);
                    if (Boolean.parseBoolean(System.getProperty("fml.ignorePatchDiscrepancies", "false"))) {
                        FMLRelaunchLog.severe("FML is going to ignore this error, note that the patch will not be applied, and there is likely to be a malfunctioning behaviour, including not running at all", new Object[0]);
                        ignoredError = true;
                        continue;
                    }
                    FMLRelaunchLog.severe("The game is going to exit, because this is a critical error, and it is very improbable that the modded game will work, please obtain clean jar files.", new Object[0]);
                    System.exit(1);
                }
            }
            synchronized (this.patcher) {
                try {
                    inputData = this.patcher.patch(inputData, patch.patch);
                }
                catch (final IOException e) {
                    FMLRelaunchLog.log(Level.SEVERE, e, "Encountered problem runtime patching class %s", name);
                }
            }
        }
        if (!ignoredError) {
            FMLRelaunchLog.fine("Successfully applied runtime patches for %s (new size %d)", mappedName, inputData.length);
        }
        if (ClassPatchManager.dumpPatched) {
            try {
                Files.write(inputData, new File(this.tempDir, mappedName));
            }
            catch (final IOException e2) {
                FMLRelaunchLog.log(Level.SEVERE, e2, "Failed to write %s to %s", mappedName, this.tempDir.getAbsolutePath());
            }
        }
        this.patchedClasses.put(name, inputData);
        return inputData;
    }
    
    public void setup(final Side side) {
        final Pattern binpatchMatcher = Pattern.compile(String.format("binpatch/%s/.*.binpatch", side.toString().toLowerCase(Locale.ENGLISH)));
        JarInputStream jis;
        try {
            final InputStream binpatchesCompressed = this.getClass().getResourceAsStream("/binpatches.pack.lzma");
            if (binpatchesCompressed == null) {
                FMLRelaunchLog.log(Level.SEVERE, "The binary patch set is missing. Either you are in a development environment, or things are not going to work!", new Object[0]);
                return;
            }
            final LzmaInputStream binpatchesDecompressed = new LzmaInputStream(binpatchesCompressed);
            final ByteArrayOutputStream jarBytes = new ByteArrayOutputStream();
            final JarOutputStream jos = new JarOutputStream(jarBytes);
            Pack200.newUnpacker().unpack((InputStream)binpatchesDecompressed, jos);
            jis = new JarInputStream(new ByteArrayInputStream(jarBytes.toByteArray()));
        }
        catch (final Exception e) {
            FMLRelaunchLog.log(Level.SEVERE, e, "Error occurred reading binary patches. Expect severe problems!", new Object[0]);
            throw Throwables.propagate((Throwable)e);
        }
        this.patches = (ListMultimap<String, ClassPatch>)ArrayListMultimap.create();
    Label_0154_Outer:
        while (true) {
            while (true) {
                try {
                    while (true) {
                        final JarEntry entry = jis.getNextJarEntry();
                        if (entry == null) {
                            break;
                        }
                        if (binpatchMatcher.matcher(entry.getName()).matches()) {
                            final ClassPatch cp = this.readPatch(entry, jis);
                            if (cp == null) {
                                continue Label_0154_Outer;
                            }
                            this.patches.put((Object)cp.sourceClassName, (Object)cp);
                        }
                        else {
                            jis.closeEntry();
                        }
                    }
                    break;
                }
                catch (final IOException e2) {
                    continue Label_0154_Outer;
                }
                continue;
            }
        }
        FMLRelaunchLog.fine("Read %d binary patches", this.patches.size());
        FMLRelaunchLog.fine("Patch list :\n\t%s", Joiner.on("\t\n").join((Iterable)this.patches.asMap().entrySet()));
        this.patchedClasses.clear();
    }
    
    private ClassPatch readPatch(final JarEntry patchEntry, final JarInputStream jis) {
        FMLRelaunchLog.finest("Reading patch data from %s", patchEntry.getName());
        ByteArrayDataInput input;
        try {
            input = ByteStreams.newDataInput(ByteStreams.toByteArray((InputStream)jis));
        }
        catch (final IOException e) {
            FMLRelaunchLog.log(Level.WARNING, e, "Unable to read binpatch file %s - ignoring", patchEntry.getName());
            return null;
        }
        final String name = input.readUTF();
        final String sourceClassName = input.readUTF();
        final String targetClassName = input.readUTF();
        final boolean exists = input.readBoolean();
        int inputChecksum = 0;
        if (exists) {
            inputChecksum = input.readInt();
        }
        final int patchLength = input.readInt();
        final byte[] patchBytes = new byte[patchLength];
        input.readFully(patchBytes);
        return new ClassPatch(name, sourceClassName, targetClassName, exists, inputChecksum, patchBytes);
    }
    
    static {
        INSTANCE = new ClassPatchManager();
        dumpPatched = Boolean.parseBoolean(System.getProperty("fml.dumpPatchedClasses", "false"));
    }
}
