// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery;

import java.util.regex.Matcher;
import cpw.mods.fml.common.ModContainerFactory;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.LoaderException;
import java.util.logging.Level;
import cpw.mods.fml.common.discovery.asm.ASMModParser;
import java.util.Arrays;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Iterator;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.FMLLog;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.ModContainer;
import java.util.List;

public class DirectoryDiscoverer implements ITypeDiscoverer
{
    private ASMDataTable table;
    
    @Override
    public List<ModContainer> discover(final ModCandidate candidate, final ASMDataTable table) {
        this.table = table;
        final List<ModContainer> found = Lists.newArrayList();
        FMLLog.fine("Examining directory %s for potential mods", candidate.getModContainer().getName());
        this.exploreFileSystem("", candidate.getModContainer(), found, candidate, null);
        for (final ModContainer mc : found) {
            table.addContainer(mc);
        }
        return found;
    }
    
    public void exploreFileSystem(final String path, final File modDir, final List<ModContainer> harvestedMods, final ModCandidate candidate, MetadataCollection mc) {
        if (path.length() == 0) {
            final File metadata = new File(modDir, "mcmod.info");
            try {
                final FileInputStream fis = new FileInputStream(metadata);
                mc = MetadataCollection.from(fis, modDir.getName());
                fis.close();
                FMLLog.fine("Found an mcmod.info file in directory %s", modDir.getName());
            }
            catch (final Exception e) {
                mc = MetadataCollection.from(null, "");
                FMLLog.fine("No mcmod.info file found in directory %s", modDir.getName());
            }
        }
        final File[] content = modDir.listFiles(new ClassFilter());
        Arrays.sort(content);
        for (final File file : content) {
            if (file.isDirectory()) {
                FMLLog.finest("Recursing into package %s", path + file.getName());
                this.exploreFileSystem(path + file.getName() + ".", file, harvestedMods, candidate, mc);
            }
            else {
                final Matcher match = DirectoryDiscoverer.classFile.matcher(file.getName());
                if (match.matches()) {
                    ASMModParser modParser = null;
                    try {
                        final FileInputStream fis2 = new FileInputStream(file);
                        modParser = new ASMModParser(fis2);
                        fis2.close();
                        candidate.addClassEntry(path + file.getName());
                    }
                    catch (final LoaderException e2) {
                        FMLLog.log(Level.SEVERE, e2, "There was a problem reading the file %s - probably this is a corrupt file", file.getPath());
                        throw e2;
                    }
                    catch (final Exception e3) {
                        Throwables.propagate((Throwable)e3);
                    }
                    modParser.validate();
                    modParser.sendToTable(this.table, candidate);
                    final ModContainer container = ModContainerFactory.instance().build(modParser, candidate.getModContainer(), candidate);
                    if (container != null) {
                        harvestedMods.add(container);
                        container.bindMetadata(mc);
                    }
                }
            }
        }
    }
    
    private class ClassFilter implements FileFilter
    {
        @Override
        public boolean accept(final File file) {
            return (file.isFile() && ITypeDiscoverer.classFile.matcher(file.getName()).find()) || file.isDirectory();
        }
    }
}
