// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.oredict;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.Iterator;
import cpw.mods.fml.common.toposort.TopologicalSort;
import java.util.List;
import java.util.Collections;
import cpw.mods.fml.common.FMLLog;
import java.util.Set;
import java.util.Map;
import java.util.Comparator;

public class RecipeSorter implements Comparator<aah>
{
    private static Map<Class, Category> categories;
    private static Map<String, Class> types;
    private static Map<String, SortEntry> entries;
    private static Map<Class, Integer> priorities;
    public static RecipeSorter INSTANCE;
    private static boolean isDirty;
    private static SortEntry before;
    private static SortEntry after;
    private static Set<Class> warned;
    
    private RecipeSorter() {
        register("minecraft:shaped", aai.class, Category.SHAPED, "before:minecraft:shapeless");
        register("minecraft:mapextending", aad.class, Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
        register("minecraft:shapeless", aaj.class, Category.SHAPELESS, "after:minecraft:shaped");
        register("minecraft:fireworks", zz.class, Category.SHAPELESS, "after:minecraft:shapeless");
        register("minecraft:armordyes", zw.class, Category.SHAPELESS, "after:minecraft:shapeless");
        register("minecraft:mapcloning", aac.class, Category.SHAPELESS, "after:minecraft:shapeless");
        register("forge:shapedore", ShapedOreRecipe.class, Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
        register("forge:shapelessore", ShapelessOreRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
    }
    
    @Override
    public int compare(final aah r1, final aah r2) {
        final Category c1 = getCategory(r1);
        final Category c2 = getCategory(r2);
        if (c1 == Category.SHAPELESS && c2 == Category.SHAPED) {
            return 1;
        }
        if (c1 == Category.SHAPED && c2 == Category.SHAPELESS) {
            return -1;
        }
        if (r2.a() < r1.a()) {
            return -1;
        }
        if (r2.a() > r1.a()) {
            return 1;
        }
        return getPriority(r2) - getPriority(r1);
    }
    
    public static void sortCraftManager() {
        bake();
        FMLLog.fine("Sorting recipies", new Object[0]);
        RecipeSorter.warned.clear();
        Collections.sort((List<Object>)aaf.a().b(), (Comparator<? super Object>)RecipeSorter.INSTANCE);
    }
    
    public static void register(final String name, final Class recipe, final Category category, final String dependancies) {
        assert category != Category.UNKNOWN : "Category must not be unknown!";
        RecipeSorter.isDirty = true;
        final SortEntry entry = new SortEntry(name, recipe, category, dependancies);
        RecipeSorter.entries.put(name, entry);
        setCategory(recipe, category);
    }
    
    public static void setCategory(final Class recipe, final Category category) {
        assert category != Category.UNKNOWN : "Category must not be unknown!";
        RecipeSorter.categories.put(recipe, category);
    }
    
    public static Category getCategory(final aah recipe) {
        return getCategory(recipe.getClass());
    }
    
    public static Category getCategory(final Class recipe) {
        Class cls = recipe;
        Category ret = RecipeSorter.categories.get(cls);
        if (ret == null) {
            cls = cls.getSuperclass();
            while (cls != Object.class) {
                ret = RecipeSorter.categories.get(cls);
                if (ret != null) {
                    RecipeSorter.categories.put(recipe, ret);
                    return ret;
                }
            }
        }
        return (ret == null) ? Category.UNKNOWN : ret;
    }
    
    private static int getPriority(final aah recipe) {
        Class cls = recipe.getClass();
        Integer ret = RecipeSorter.priorities.get(cls);
        if (ret == null) {
            final RecipeSorter instance = RecipeSorter.INSTANCE;
            if (!RecipeSorter.warned.contains(cls)) {
                FMLLog.fine("  Unknown recipe class! %s Modder please refer to %s", cls.getName(), RecipeSorter.class.getName());
                RecipeSorter.warned.add(cls);
            }
            cls = cls.getSuperclass();
            while (cls != Object.class) {
                ret = RecipeSorter.priorities.get(cls);
                if (ret != null) {
                    RecipeSorter.priorities.put(recipe.getClass(), ret);
                    FMLLog.fine("    Parent Found: %d - %s", ret, cls.getName());
                    return ret;
                }
            }
        }
        return (ret == null) ? 0 : ret;
    }
    
    private static void bake() {
        if (!RecipeSorter.isDirty) {
            return;
        }
        FMLLog.fine("Forge RecipeSorter Baking:", new Object[0]);
        final TopologicalSort.DirectedGraph<SortEntry> sorter = new TopologicalSort.DirectedGraph<SortEntry>();
        sorter.addNode(RecipeSorter.before);
        sorter.addNode(RecipeSorter.after);
        sorter.addEdge(RecipeSorter.before, RecipeSorter.after);
        for (final Map.Entry<String, SortEntry> entry : RecipeSorter.entries.entrySet()) {
            sorter.addNode(entry.getValue());
        }
        for (final Map.Entry<String, SortEntry> e : RecipeSorter.entries.entrySet()) {
            final SortEntry entry2 = e.getValue();
            boolean postAdded = false;
            sorter.addEdge(RecipeSorter.before, entry2);
            for (final String dep : entry2.after) {
                if (RecipeSorter.entries.containsKey(dep)) {
                    sorter.addEdge(RecipeSorter.entries.get(dep), entry2);
                }
            }
            for (final String dep : entry2.before) {
                postAdded = true;
                sorter.addEdge(entry2, RecipeSorter.after);
                if (RecipeSorter.entries.containsKey(dep)) {
                    sorter.addEdge(entry2, RecipeSorter.entries.get(dep));
                }
            }
            if (!postAdded) {
                sorter.addEdge(entry2, RecipeSorter.after);
            }
        }
        final List<SortEntry> sorted = TopologicalSort.topologicalSort(sorter);
        int x = sorted.size();
        for (final SortEntry entry3 : sorted) {
            FMLLog.fine("  %d: %s", x, entry3);
            RecipeSorter.priorities.put(entry3.cls, x--);
        }
    }
    
    static {
        RecipeSorter.categories = Maps.newHashMap();
        RecipeSorter.types = Maps.newHashMap();
        RecipeSorter.entries = Maps.newHashMap();
        RecipeSorter.priorities = Maps.newHashMap();
        RecipeSorter.INSTANCE = new RecipeSorter();
        RecipeSorter.isDirty = true;
        RecipeSorter.before = new SortEntry("Before", (Class)null, Category.UNKNOWN, "");
        RecipeSorter.after = new SortEntry("After", (Class)null, Category.UNKNOWN, "");
        RecipeSorter.warned = Sets.newHashSet();
    }
    
    public enum Category
    {
        UNKNOWN, 
        SHAPELESS, 
        SHAPED;
    }
    
    private static class SortEntry
    {
        private String name;
        private Class cls;
        private Category cat;
        List<String> before;
        List<String> after;
        
        private SortEntry(final String name, final Class cls, final Category cat, final String deps) {
            this.before = Lists.newArrayList();
            this.after = Lists.newArrayList();
            this.name = name;
            this.cls = cls;
            this.cat = cat;
            this.parseDepends(deps);
        }
        
        private void parseDepends(final String deps) {
            if (deps.isEmpty()) {
                return;
            }
            for (final String dep : deps.split(" ")) {
                if (dep.startsWith("before:")) {
                    this.before.add(dep.substring(7));
                }
                else {
                    if (!dep.startsWith("after:")) {
                        throw new IllegalArgumentException("Invalid dependancy: " + dep);
                    }
                    this.after.add(dep.substring(6));
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append("RecipeEntry(\"").append(this.name).append("\", ");
            buf.append(this.cat.name()).append(", ");
            buf.append((this.cls == null) ? "" : this.cls.getName()).append(")");
            if (this.before.size() > 0) {
                buf.append(" Before: ").append(Joiner.on(", ").join((Iterable)this.before));
            }
            if (this.after.size() > 0) {
                buf.append(" After: ").append(Joiner.on(", ").join((Iterable)this.after));
            }
            return buf.toString();
        }
        
        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}
