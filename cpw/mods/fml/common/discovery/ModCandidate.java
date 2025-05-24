// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery;

import cpw.mods.fml.common.FMLLog;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.ModContainer;
import java.util.Set;
import cpw.mods.fml.common.discovery.asm.ASMModParser;
import java.util.List;
import java.io.File;

public class ModCandidate
{
    private File classPathRoot;
    private File modContainer;
    private ContainerType sourceType;
    private boolean classpath;
    private List<String> baseModTypes;
    private boolean isMinecraft;
    private List<ASMModParser> baseModCandidateTypes;
    private Set<String> foundClasses;
    private List<ModContainer> mods;
    private List<String> packages;
    private ASMDataTable table;
    
    public ModCandidate(final File classPathRoot, final File modContainer, final ContainerType sourceType) {
        this(classPathRoot, modContainer, sourceType, false, false);
    }
    
    public ModCandidate(final File classPathRoot, final File modContainer, final ContainerType sourceType, final boolean isMinecraft, final boolean classpath) {
        this.baseModTypes = Lists.newArrayList();
        this.baseModCandidateTypes = Lists.newArrayListWithCapacity(1);
        this.foundClasses = Sets.newHashSet();
        this.packages = Lists.newArrayList();
        this.classPathRoot = classPathRoot;
        this.modContainer = modContainer;
        this.sourceType = sourceType;
        this.isMinecraft = isMinecraft;
        this.classpath = classpath;
    }
    
    public File getClassPathRoot() {
        return this.classPathRoot;
    }
    
    public File getModContainer() {
        return this.modContainer;
    }
    
    public ContainerType getSourceType() {
        return this.sourceType;
    }
    
    public List<ModContainer> explore(final ASMDataTable table) {
        this.table = table;
        this.mods = this.sourceType.findMods(this, table);
        if (!this.baseModCandidateTypes.isEmpty()) {
            FMLLog.info("Attempting to reparse the mod container %s", this.getModContainer().getName());
            this.mods = this.sourceType.findMods(this, table);
        }
        return this.mods;
    }
    
    public void addClassEntry(final String name) {
        String className = name.substring(0, name.lastIndexOf(46));
        this.foundClasses.add(className.replace('.', '/'));
        className = className.replace('/', '.');
        final int pkgIdx = className.lastIndexOf(46);
        if (pkgIdx > -1) {
            final String pkg = className.substring(0, pkgIdx);
            this.packages.add(pkg);
            this.table.registerPackage(this, pkg);
        }
    }
    
    public boolean isClasspath() {
        return this.classpath;
    }
    
    public void rememberBaseModType(final String className) {
        this.baseModTypes.add(className);
    }
    
    public List<String> getRememberedBaseMods() {
        return this.baseModTypes;
    }
    
    public boolean isMinecraftJar() {
        return this.isMinecraft;
    }
    
    public void rememberModCandidateType(final ASMModParser modParser) {
        this.baseModCandidateTypes.add(modParser);
    }
    
    public Set<String> getClassList() {
        return this.foundClasses;
    }
    
    public List<ModContainer> getContainedMods() {
        return this.mods;
    }
    
    public List<String> getContainedPackages() {
        return this.packages;
    }
}
