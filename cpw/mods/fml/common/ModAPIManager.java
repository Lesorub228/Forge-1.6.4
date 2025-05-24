// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import java.io.File;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import com.google.common.base.Function;
import java.util.List;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.functions.ModIdFunction;
import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.versioning.VersionParser;
import com.google.common.collect.Maps;
import java.util.Map;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.asm.transformers.ModAPITransformer;

public class ModAPIManager
{
    public static final ModAPIManager INSTANCE;
    private ModAPITransformer transformer;
    private ASMDataTable dataTable;
    private Map<String, APIContainer> apiContainers;
    
    public void registerDataTableAndParseAPI(final ASMDataTable dataTable) {
        this.dataTable = dataTable;
        final Set<ASMDataTable.ASMData> apiList = dataTable.getAll("cpw.mods.fml.common.API");
        this.apiContainers = Maps.newHashMap();
        for (final ASMDataTable.ASMData data : apiList) {
            final Map<String, Object> annotationInfo = data.getAnnotationInfo();
            final String apiPackage = data.getClassName().substring(0, data.getClassName().indexOf(".package-info"));
            final String providedAPI = annotationInfo.get("provides");
            final String apiOwner = annotationInfo.get("owner");
            final String apiVersion = annotationInfo.get("apiVersion");
            APIContainer container = this.apiContainers.get(providedAPI);
            if (container == null) {
                container = new APIContainer(providedAPI, apiVersion, data.getCandidate().getModContainer(), VersionParser.parseVersionReference(apiOwner));
                this.apiContainers.put(providedAPI, container);
            }
            else {
                container.validate(providedAPI, apiOwner, apiVersion);
            }
            container.addOwnedPackage(apiPackage);
            for (final ModContainer mc : data.getCandidate().getContainedMods()) {
                final String embeddedIn = mc.getModId();
                if (container.currentReferents.contains(embeddedIn)) {
                    continue;
                }
                FMLLog.fine("Found API %s (owned by %s providing %s) embedded in %s", apiPackage, apiOwner, providedAPI, embeddedIn);
                if (embeddedIn.equals(apiOwner)) {
                    continue;
                }
                container.addAPIReference(embeddedIn);
            }
        }
        for (final APIContainer container2 : this.apiContainers.values()) {
            for (final String pkg : container2.packages) {
                final Set<ModCandidate> candidates = dataTable.getCandidatesFor(pkg);
                for (final ModCandidate candidate : candidates) {
                    final List<String> candidateIds = Lists.transform((List)candidate.getContainedMods(), (Function)new ModIdFunction());
                    if (!candidateIds.contains(container2.ownerMod.getLabel()) && !container2.currentReferents.containsAll(candidateIds)) {
                        FMLLog.info("Found mod(s) %s containing declared API package %s (owned by %s) without associated API reference", candidateIds, pkg, container2.ownerMod);
                        container2.addAPIReferences(candidateIds);
                    }
                }
            }
            if (this.apiContainers.containsKey(container2.ownerMod.getLabel())) {
                ArtifactVersion owner = container2.ownerMod;
                do {
                    final APIContainer parent = this.apiContainers.get(owner.getLabel());
                    FMLLog.finest("Removing upstream parent %s from %s", parent.ownerMod.getLabel(), container2);
                    container2.currentReferents.remove(parent.ownerMod.getLabel());
                    container2.referredMods.remove(parent.ownerMod);
                    owner = parent.ownerMod;
                } while (this.apiContainers.containsKey(owner.getLabel()));
            }
            FMLLog.fine("Creating API container dummy for API %s: owner: %s, dependents: %s", container2.providedAPI, container2.ownerMod, container2.referredMods);
        }
    }
    
    public void manageAPI(final ModClassLoader modClassLoader, final ModDiscoverer discoverer) {
        this.registerDataTableAndParseAPI(discoverer.getASMTable());
        this.transformer = modClassLoader.addModAPITransformer(this.dataTable);
    }
    
    public void injectAPIModContainers(final List<ModContainer> mods, final Map<String, ModContainer> nameLookup) {
        mods.addAll(this.apiContainers.values());
        nameLookup.putAll(this.apiContainers);
    }
    
    public void cleanupAPIContainers(final List<ModContainer> mods) {
        mods.removeAll(this.apiContainers.values());
    }
    
    public boolean hasAPI(final String modId) {
        return this.apiContainers.containsKey(modId);
    }
    
    static {
        INSTANCE = new ModAPIManager();
    }
    
    private static class APIContainer extends DummyModContainer
    {
        private List<ArtifactVersion> referredMods;
        private ArtifactVersion ownerMod;
        private ArtifactVersion ourVersion;
        private String providedAPI;
        private File source;
        private String version;
        private Set<String> currentReferents;
        private Set<String> packages;
        
        public APIContainer(final String providedAPI, final String apiVersion, final File source, final ArtifactVersion ownerMod) {
            this.providedAPI = providedAPI;
            this.version = apiVersion;
            this.ownerMod = ownerMod;
            this.ourVersion = new DefaultArtifactVersion(providedAPI, apiVersion);
            this.referredMods = Lists.newArrayList();
            this.source = source;
            this.currentReferents = Sets.newHashSet();
            this.packages = Sets.newHashSet();
        }
        
        @Override
        public File getSource() {
            return this.source;
        }
        
        @Override
        public String getVersion() {
            return this.version;
        }
        
        @Override
        public String getName() {
            return "API: " + this.providedAPI;
        }
        
        @Override
        public String getModId() {
            return "API:" + this.providedAPI;
        }
        
        @Override
        public List<ArtifactVersion> getDependants() {
            return this.referredMods;
        }
        
        @Override
        public List<ArtifactVersion> getDependencies() {
            return (List<ArtifactVersion>)ImmutableList.of((Object)this.ownerMod);
        }
        
        @Override
        public ArtifactVersion getProcessedVersion() {
            return this.ourVersion;
        }
        
        public void validate(final String providedAPI, final String apiOwner, final String apiVersion) {
        }
        
        @Override
        public String toString() {
            return "APIContainer{" + this.providedAPI + ":" + this.version + "}";
        }
        
        public void addAPIReference(final String embedded) {
            if (this.currentReferents.add(embedded)) {
                this.referredMods.add(VersionParser.parseVersionReference(embedded));
            }
        }
        
        public void addOwnedPackage(final String apiPackage) {
            this.packages.add(apiPackage);
        }
        
        public void addAPIReferences(final List<String> candidateIds) {
            for (final String modId : candidateIds) {
                this.addAPIReference(modId);
            }
        }
    }
}
