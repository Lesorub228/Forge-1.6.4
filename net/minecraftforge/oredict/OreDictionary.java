// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.oredict;

import net.minecraftforge.event.Event;
import net.minecraftforge.common.MinecraftForge;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

public class OreDictionary
{
    private static boolean hasInit;
    private static int maxID;
    private static HashMap<String, Integer> oreIDs;
    private static HashMap<Integer, ArrayList<ye>> oreStacks;
    public static final int WILDCARD_VALUE = 32767;
    
    public static void initVanillaEntries() {
        if (!OreDictionary.hasInit) {
            registerOre("logWood", new ye(aqz.O, 1, 32767));
            registerOre("plankWood", new ye(aqz.C, 1, 32767));
            registerOre("slabWood", new ye((aqz)aqz.bT, 1, 32767));
            registerOre("stairWood", aqz.ay);
            registerOre("stairWood", aqz.cc);
            registerOre("stairWood", aqz.cd);
            registerOre("stairWood", aqz.cb);
            registerOre("stickWood", yc.F);
            registerOre("treeSapling", new ye(aqz.D, 1, 32767));
            registerOre("treeLeaves", new ye((aqz)aqz.P, 1, 32767));
            registerOre("oreGold", aqz.L);
            registerOre("oreIron", aqz.M);
            registerOre("oreLapis", aqz.S);
            registerOre("oreDiamond", aqz.aB);
            registerOre("oreRedstone", aqz.aS);
            registerOre("oreEmerald", aqz.bW);
            registerOre("oreQuartz", aqz.cu);
            registerOre("stone", aqz.y);
            registerOre("cobblestone", aqz.B);
            registerOre("record", yc.cj);
            registerOre("record", yc.ck);
            registerOre("record", yc.cl);
            registerOre("record", yc.cm);
            registerOre("record", yc.cn);
            registerOre("record", yc.co);
            registerOre("record", yc.cp);
            registerOre("record", yc.cq);
            registerOre("record", yc.cr);
            registerOre("record", yc.cs);
            registerOre("record", yc.ct);
            registerOre("record", yc.cu);
        }
        final Map<ye, String> replacements = new HashMap<ye, String>();
        replacements.put(new ye(yc.F), "stickWood");
        replacements.put(new ye(aqz.C), "plankWood");
        replacements.put(new ye(aqz.C, 1, 32767), "plankWood");
        replacements.put(new ye(aqz.y), "stone");
        replacements.put(new ye(aqz.y, 1, 32767), "stone");
        replacements.put(new ye(aqz.B), "cobblestone");
        replacements.put(new ye(aqz.B, 1, 32767), "cobblestone");
        final String[] dyes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };
        for (int i = 0; i < 16; ++i) {
            final ye dye = new ye(yc.aY, 1, i);
            if (!OreDictionary.hasInit) {
                registerOre(dyes[i], dye);
            }
            replacements.put(dye, dyes[i]);
        }
        OreDictionary.hasInit = true;
        final ye[] replaceStacks = replacements.keySet().toArray(new ye[replacements.keySet().size()]);
        final ye[] exclusions = { new ye(aqz.T), new ye(yc.be), new ye(aqz.br), new ye((aqz)aqz.ap), new ye(aqz.aM), new ye(aqz.cg), new ye(aqz.ay), new ye(aqz.cc), new ye(aqz.cd), new ye(aqz.cb) };
        final List recipes = aaf.a().b();
        final List<aah> recipesToRemove = new ArrayList<aah>();
        final List<aah> recipesToAdd = new ArrayList<aah>();
        for (final Object obj : recipes) {
            if (obj instanceof aai) {
                final aai recipe = (aai)obj;
                final ye output = recipe.b();
                if (output != null && containsMatch(false, exclusions, output)) {
                    continue;
                }
                if (!containsMatch(true, recipe.d, replaceStacks)) {
                    continue;
                }
                recipesToRemove.add((aah)recipe);
                recipesToAdd.add((aah)new ShapedOreRecipe(recipe, replacements));
            }
            else {
                if (!(obj instanceof aaj)) {
                    continue;
                }
                final aaj recipe2 = (aaj)obj;
                final ye output = recipe2.b();
                if (output != null && containsMatch(false, exclusions, output)) {
                    continue;
                }
                if (!containsMatch(true, recipe2.b.toArray(new ye[recipe2.b.size()]), replaceStacks)) {
                    continue;
                }
                recipesToRemove.add((aah)obj);
                final aah newRecipe = (aah)new ShapelessOreRecipe(recipe2, replacements);
                recipesToAdd.add(newRecipe);
            }
        }
        recipes.removeAll(recipesToRemove);
        recipes.addAll(recipesToAdd);
        if (recipesToRemove.size() > 0) {
            System.out.println("Replaced " + recipesToRemove.size() + " ore recipies");
        }
    }
    
    public static int getOreID(final String name) {
        Integer val = OreDictionary.oreIDs.get(name);
        if (val == null) {
            val = OreDictionary.maxID++;
            OreDictionary.oreIDs.put(name, val);
            OreDictionary.oreStacks.put(val, new ArrayList<ye>());
        }
        return val;
    }
    
    public static String getOreName(final int id) {
        for (final Map.Entry<String, Integer> entry : OreDictionary.oreIDs.entrySet()) {
            if (id == entry.getValue()) {
                return entry.getKey();
            }
        }
        return "Unknown";
    }
    
    public static int getOreID(final ye itemStack) {
        if (itemStack == null) {
            return -1;
        }
        for (final Map.Entry<Integer, ArrayList<ye>> ore : OreDictionary.oreStacks.entrySet()) {
            for (final ye target : ore.getValue()) {
                if (itemStack.d == target.d && (target.k() == 32767 || itemStack.k() == target.k())) {
                    return ore.getKey();
                }
            }
        }
        return -1;
    }
    
    public static ArrayList<ye> getOres(final String name) {
        return getOres(getOreID(name));
    }
    
    public static String[] getOreNames() {
        return OreDictionary.oreIDs.keySet().toArray(new String[OreDictionary.oreIDs.keySet().size()]);
    }
    
    public static ArrayList<ye> getOres(final Integer id) {
        ArrayList<ye> val = OreDictionary.oreStacks.get(id);
        if (val == null) {
            val = new ArrayList<ye>();
            OreDictionary.oreStacks.put(id, val);
        }
        return val;
    }
    
    private static boolean containsMatch(final boolean strict, final ye[] inputs, final ye... targets) {
        for (final ye input : inputs) {
            for (final ye target : targets) {
                if (itemMatches(target, input, strict)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean itemMatches(final ye target, final ye input, final boolean strict) {
        return (input != null || target == null) && (input == null || target != null) && target.d == input.d && ((target.k() == 32767 && !strict) || target.k() == input.k());
    }
    
    public static void registerOre(final String name, final yc ore) {
        registerOre(name, new ye(ore));
    }
    
    public static void registerOre(final String name, final aqz ore) {
        registerOre(name, new ye(ore));
    }
    
    public static void registerOre(final String name, final ye ore) {
        registerOre(name, getOreID(name), ore);
    }
    
    public static void registerOre(final int id, final yc ore) {
        registerOre(id, new ye(ore));
    }
    
    public static void registerOre(final int id, final aqz ore) {
        registerOre(id, new ye(ore));
    }
    
    public static void registerOre(final int id, final ye ore) {
        registerOre(getOreName(id), id, ore);
    }
    
    private static void registerOre(final String name, final int id, ye ore) {
        final ArrayList<ye> ores = getOres(id);
        ore = ore.m();
        ores.add(ore);
        MinecraftForge.EVENT_BUS.post(new OreRegisterEvent(name, ore));
    }
    
    static {
        OreDictionary.hasInit = false;
        OreDictionary.maxID = 0;
        OreDictionary.oreIDs = new HashMap<String, Integer>();
        OreDictionary.oreStacks = new HashMap<Integer, ArrayList<ye>>();
        initVanillaEntries();
    }
    
    public static class OreRegisterEvent extends Event
    {
        public final String Name;
        public final ye Ore;
        
        public OreRegisterEvent(final String name, final ye ore) {
            this.Name = name;
            this.Ore = ore;
        }
    }
}
