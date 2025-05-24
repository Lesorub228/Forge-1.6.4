// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.toposort;

import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import java.util.HashMap;
import cpw.mods.fml.common.ModAPIManager;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.DummyModContainer;
import java.util.Map;
import java.util.List;
import cpw.mods.fml.common.ModContainer;

public class ModSorter
{
    private TopologicalSort.DirectedGraph<ModContainer> modGraph;
    private ModContainer beforeAll;
    private ModContainer afterAll;
    private ModContainer before;
    private ModContainer after;
    
    public ModSorter(final List<ModContainer> modList, final Map<String, ModContainer> nameLookup) {
        this.beforeAll = new DummyModContainer("BeforeAll");
        this.afterAll = new DummyModContainer("AfterAll");
        this.before = new DummyModContainer("Before");
        this.after = new DummyModContainer("After");
        final HashMap<String, ModContainer> sortingNameLookup = Maps.newHashMap((Map)nameLookup);
        ModAPIManager.INSTANCE.injectAPIModContainers(modList, sortingNameLookup);
        this.buildGraph(modList, sortingNameLookup);
    }
    
    private void buildGraph(final List<ModContainer> modList, final Map<String, ModContainer> nameLookup) {
        (this.modGraph = new TopologicalSort.DirectedGraph<ModContainer>()).addNode(this.beforeAll);
        this.modGraph.addNode(this.before);
        this.modGraph.addNode(this.afterAll);
        this.modGraph.addNode(this.after);
        this.modGraph.addEdge(this.before, this.after);
        this.modGraph.addEdge(this.beforeAll, this.before);
        this.modGraph.addEdge(this.after, this.afterAll);
        for (final ModContainer mod : modList) {
            this.modGraph.addNode(mod);
        }
        for (final ModContainer mod : modList) {
            if (mod.isImmutable()) {
                this.modGraph.addEdge(this.beforeAll, mod);
                this.modGraph.addEdge(mod, this.before);
            }
            else {
                boolean preDepAdded = false;
                boolean postDepAdded = false;
                for (final ArtifactVersion dep : mod.getDependencies()) {
                    preDepAdded = true;
                    final String modid = dep.getLabel();
                    if (modid.equals("*")) {
                        this.modGraph.addEdge(mod, this.afterAll);
                        this.modGraph.addEdge(this.after, mod);
                        postDepAdded = true;
                    }
                    else {
                        this.modGraph.addEdge(this.before, mod);
                        if (!nameLookup.containsKey(modid) && !Loader.isModLoaded(modid)) {
                            continue;
                        }
                        this.modGraph.addEdge(nameLookup.get(modid), mod);
                    }
                }
                for (final ArtifactVersion dep : mod.getDependants()) {
                    postDepAdded = true;
                    final String modid = dep.getLabel();
                    if (modid.equals("*")) {
                        this.modGraph.addEdge(this.beforeAll, mod);
                        this.modGraph.addEdge(mod, this.before);
                        preDepAdded = true;
                    }
                    else {
                        this.modGraph.addEdge(mod, this.after);
                        if (!Loader.isModLoaded(modid)) {
                            continue;
                        }
                        this.modGraph.addEdge(mod, nameLookup.get(modid));
                    }
                }
                if (!preDepAdded) {
                    this.modGraph.addEdge(this.before, mod);
                }
                if (postDepAdded) {
                    continue;
                }
                this.modGraph.addEdge(mod, this.after);
            }
        }
    }
    
    public List<ModContainer> sort() {
        final List<ModContainer> sortedList = TopologicalSort.topologicalSort(this.modGraph);
        sortedList.removeAll(Arrays.asList(this.beforeAll, this.before, this.after, this.afterAll));
        return sortedList;
    }
}
