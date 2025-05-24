// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery;

import java.util.Iterator;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.LoaderException;
import java.util.logging.Level;
import java.util.Collection;
import cpw.mods.fml.common.ModContainer;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.Comparator;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.CoreModManager;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.ModClassLoader;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class ModDiscoverer
{
    private static Pattern zipJar;
    private List<ModCandidate> candidates;
    private ASMDataTable dataTable;
    private List<File> nonModLibs;
    
    public ModDiscoverer() {
        this.candidates = Lists.newArrayList();
        this.dataTable = new ASMDataTable();
        this.nonModLibs = Lists.newArrayList();
    }
    
    public void findClasspathMods(final ModClassLoader modClassLoader) {
        final List<String> knownLibraries = (List<String>)ImmutableList.builder().addAll((Iterable)modClassLoader.getDefaultLibraries()).addAll((Iterable)CoreModManager.getLoadedCoremods()).addAll((Iterable)CoreModManager.getReparseableCoremods()).build();
        final File[] minecraftSources = modClassLoader.getParentSources();
        if (minecraftSources.length == 1 && minecraftSources[0].isFile()) {
            FMLLog.fine("Minecraft is a file at %s, loading", minecraftSources[0].getAbsolutePath());
            this.candidates.add(new ModCandidate(minecraftSources[0], minecraftSources[0], ContainerType.JAR, true, true));
        }
        else {
            for (int i = 0; i < minecraftSources.length; ++i) {
                if (minecraftSources[i].isFile()) {
                    if (knownLibraries.contains(minecraftSources[i].getName())) {
                        FMLLog.finer("Skipping known library file %s", minecraftSources[i].getAbsolutePath());
                    }
                    else {
                        FMLLog.fine("Found a minecraft related file at %s, examining for mod candidates", minecraftSources[i].getAbsolutePath());
                        this.candidates.add(new ModCandidate(minecraftSources[i], minecraftSources[i], ContainerType.JAR, i == 0, true));
                    }
                }
                else if (minecraftSources[i].isDirectory()) {
                    FMLLog.fine("Found a minecraft related directory at %s, examining for mod candidates", minecraftSources[i].getAbsolutePath());
                    this.candidates.add(new ModCandidate(minecraftSources[i], minecraftSources[i], ContainerType.DIR, i == 0, true));
                }
            }
        }
    }
    
    public void findModDirMods(final File modsDir) {
        final File[] modList = modsDir.listFiles();
        Arrays.sort(modList, new Comparator<File>() {
            @Override
            public int compare(final File o1, final File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        for (final File modFile : modList) {
            if (CoreModManager.getLoadedCoremods().contains(modFile.getName())) {
                FMLLog.finer("Skipping already parsed coremod or tweaker %s", modFile.getName());
            }
            else if (modFile.isDirectory()) {
                FMLLog.fine("Found a candidate mod directory %s", modFile.getName());
                this.candidates.add(new ModCandidate(modFile, modFile, ContainerType.DIR));
            }
            else {
                final Matcher matcher = ModDiscoverer.zipJar.matcher(modFile.getName());
                if (matcher.matches()) {
                    FMLLog.fine("Found a candidate zip or jar file %s", matcher.group(0));
                    this.candidates.add(new ModCandidate(modFile, modFile, ContainerType.JAR));
                }
                else {
                    FMLLog.fine("Ignoring unknown file %s in mods directory", modFile.getName());
                }
            }
        }
    }
    
    public List<ModContainer> identifyMods() {
        final List<ModContainer> modList = Lists.newArrayList();
        for (final ModCandidate candidate : this.candidates) {
            try {
                final List<ModContainer> mods = candidate.explore(this.dataTable);
                if (mods.isEmpty() && !candidate.isClasspath()) {
                    this.nonModLibs.add(candidate.getModContainer());
                }
                else {
                    modList.addAll(mods);
                }
            }
            catch (final LoaderException le) {
                FMLLog.log(Level.WARNING, le, "Identified a problem with the mod candidate %s, ignoring this source", candidate.getModContainer());
            }
            catch (final Throwable t) {
                Throwables.propagate(t);
            }
        }
        return modList;
    }
    
    public ASMDataTable getASMTable() {
        return this.dataTable;
    }
    
    public List<File> getNonModLibs() {
        return this.nonModLibs;
    }
    
    static {
        ModDiscoverer.zipJar = Pattern.compile("(.+).(zip|jar)$");
    }
}
