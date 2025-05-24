// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery;

import java.util.regex.Matcher;
import java.util.Iterator;
import cpw.mods.fml.common.ModContainerFactory;
import cpw.mods.fml.common.LoaderException;
import java.util.logging.Level;
import cpw.mods.fml.common.discovery.asm.ASMModParser;
import java.util.zip.ZipEntry;
import java.util.Collections;
import java.io.InputStream;
import cpw.mods.fml.common.MetadataCollection;
import java.util.jar.JarFile;
import cpw.mods.fml.common.FMLLog;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.ModContainer;
import java.util.List;

public class JarDiscoverer implements ITypeDiscoverer
{
    @Override
    public List<ModContainer> discover(final ModCandidate candidate, final ASMDataTable table) {
        final List<ModContainer> foundMods = Lists.newArrayList();
        FMLLog.fine("Examining file %s for potential mods", candidate.getModContainer().getName());
        JarFile jar = null;
        try {
            jar = new JarFile(candidate.getModContainer());
            if (jar.getManifest() != null && (jar.getManifest().getMainAttributes().get("FMLCorePlugin") != null || jar.getManifest().getMainAttributes().get("TweakClass") != null)) {
                FMLLog.finest("Ignoring coremod or tweak system %s", candidate.getModContainer());
                return foundMods;
            }
            final ZipEntry modInfo = jar.getEntry("mcmod.info");
            MetadataCollection mc = null;
            if (modInfo != null) {
                FMLLog.finer("Located mcmod.info file in file %s", candidate.getModContainer().getName());
                mc = MetadataCollection.from(jar.getInputStream(modInfo), candidate.getModContainer().getName());
            }
            else {
                FMLLog.fine("The mod container %s appears to be missing an mcmod.info file", candidate.getModContainer().getName());
                mc = MetadataCollection.from(null, "");
            }
            for (final ZipEntry ze : Collections.list(jar.entries())) {
                if (ze.getName() != null && ze.getName().startsWith("__MACOSX")) {
                    continue;
                }
                final Matcher match = JarDiscoverer.classFile.matcher(ze.getName());
                if (!match.matches()) {
                    continue;
                }
                ASMModParser modParser;
                try {
                    modParser = new ASMModParser(jar.getInputStream(ze));
                    candidate.addClassEntry(ze.getName());
                }
                catch (final LoaderException e) {
                    FMLLog.log(Level.SEVERE, e, "There was a problem reading the entry %s in the jar %s - probably a corrupt zip", ze.getName(), candidate.getModContainer().getPath());
                    jar.close();
                    throw e;
                }
                modParser.validate();
                modParser.sendToTable(table, candidate);
                final ModContainer container = ModContainerFactory.instance().build(modParser, candidate.getModContainer(), candidate);
                if (container == null) {
                    continue;
                }
                table.addContainer(container);
                foundMods.add(container);
                container.bindMetadata(mc);
            }
        }
        catch (final Exception e2) {
            FMLLog.log(Level.WARNING, e2, "Zip file %s failed to read properly, it will be ignored", candidate.getModContainer().getName());
        }
        finally {
            if (jar != null) {
                try {
                    jar.close();
                }
                catch (final Exception ex) {}
            }
        }
        return foundMods;
    }
}
