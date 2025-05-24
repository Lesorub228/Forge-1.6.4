// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

public class ChestGenHooks
{
    public static final String MINESHAFT_CORRIDOR = "mineshaftCorridor";
    public static final String PYRAMID_DESERT_CHEST = "pyramidDesertyChest";
    public static final String PYRAMID_JUNGLE_CHEST = "pyramidJungleChest";
    public static final String PYRAMID_JUNGLE_DISPENSER = "pyramidJungleDispenser";
    public static final String STRONGHOLD_CORRIDOR = "strongholdCorridor";
    public static final String STRONGHOLD_LIBRARY = "strongholdLibrary";
    public static final String STRONGHOLD_CROSSING = "strongholdCrossing";
    public static final String VILLAGE_BLACKSMITH = "villageBlacksmith";
    public static final String BONUS_CHEST = "bonusChest";
    public static final String DUNGEON_CHEST = "dungeonChest";
    private static final HashMap<String, ChestGenHooks> chestInfo;
    private static boolean hasInit;
    private String category;
    private int countMin;
    private int countMax;
    ArrayList<mk> contents;
    
    private static void init() {
        if (ChestGenHooks.hasInit) {
            return;
        }
        ChestGenHooks.hasInit = true;
        addInfo("mineshaftCorridor", agh.a, 3, 7);
        addInfo("pyramidDesertyChest", ahl.i, 2, 7);
        addInfo("pyramidJungleChest", ahm.l, 2, 7);
        addInfo("pyramidJungleDispenser", ahm.m, 2, 2);
        addInfo("strongholdCorridor", ahw.a, 2, 4);
        addInfo("strongholdLibrary", aia.a, 1, 5);
        addInfo("strongholdCrossing", aif.b, 1, 5);
        addInfo("villageBlacksmith", aji.a, 3, 9);
        addInfo("bonusChest", js.T, 10, 10);
        addInfo("dungeonChest", afp.a, 8, 8);
        final ye book = new ye((yc)yc.bY, 1, 0);
        final mk tmp = new mk(book, 1, 1, 1);
        getInfo("mineshaftCorridor").addItem(tmp);
        getInfo("pyramidDesertyChest").addItem(tmp);
        getInfo("pyramidJungleChest").addItem(tmp);
        getInfo("strongholdCorridor").addItem(tmp);
        getInfo("strongholdLibrary").addItem(new mk(book, 1, 5, 2));
        getInfo("strongholdCrossing").addItem(tmp);
        getInfo("dungeonChest").addItem(tmp);
    }
    
    static void addDungeonLoot(final ChestGenHooks dungeon, final ye item, final int weight, final int min, final int max) {
        dungeon.addItem(new mk(item, min, max, weight));
    }
    
    private static void addInfo(final String category, final mk[] items, final int min, final int max) {
        ChestGenHooks.chestInfo.put(category, new ChestGenHooks(category, items, min, max));
    }
    
    public static ChestGenHooks getInfo(final String category) {
        if (!ChestGenHooks.chestInfo.containsKey(category)) {
            ChestGenHooks.chestInfo.put(category, new ChestGenHooks(category));
        }
        return ChestGenHooks.chestInfo.get(category);
    }
    
    public static ye[] generateStacks(final Random rand, final ye source, final int min, final int max) {
        final int count = min + rand.nextInt(max - min + 1);
        ye[] ret;
        if (source.b() == null) {
            ret = new ye[0];
        }
        else if (count > source.e()) {
            ret = new ye[count];
            for (int x = 0; x < count; ++x) {
                ret[x] = source.m();
                ret[x].b = 1;
            }
        }
        else {
            ret = new ye[] { source.m() };
            ret[0].b = count;
        }
        return ret;
    }
    
    public static mk[] getItems(final String category, final Random rnd) {
        return getInfo(category).getItems(rnd);
    }
    
    public static int getCount(final String category, final Random rand) {
        return getInfo(category).getCount(rand);
    }
    
    public static void addItem(final String category, final mk item) {
        getInfo(category).addItem(item);
    }
    
    public static void removeItem(final String category, final ye item) {
        getInfo(category).removeItem(item);
    }
    
    public static ye getOneItem(final String category, final Random rand) {
        return getInfo(category).getOneItem(rand);
    }
    
    public ChestGenHooks(final String category) {
        this.countMin = 0;
        this.countMax = 0;
        this.contents = new ArrayList<mk>();
        this.category = category;
    }
    
    public ChestGenHooks(final String category, final mk[] items, final int min, final int max) {
        this(category);
        for (final mk item : items) {
            this.contents.add(item);
        }
        this.countMin = min;
        this.countMax = max;
    }
    
    public void addItem(final mk item) {
        this.contents.add(item);
    }
    
    public void removeItem(final ye item) {
        final Iterator<mk> itr = this.contents.iterator();
        while (itr.hasNext()) {
            final mk cont = itr.next();
            if (item.a(cont.b) || (item.k() == 32767 && item.d == cont.b.d)) {
                itr.remove();
            }
        }
    }
    
    public mk[] getItems(final Random rnd) {
        final ArrayList<mk> ret = new ArrayList<mk>();
        for (final mk orig : this.contents) {
            final yc item = orig.b.b();
            if (item != null) {
                final mk n = item.getChestGenBase(this, rnd, orig);
                if (n == null) {
                    continue;
                }
                ret.add(n);
            }
        }
        return ret.toArray(new mk[ret.size()]);
    }
    
    public int getCount(final Random rand) {
        return (this.countMin < this.countMax) ? (this.countMin + rand.nextInt(this.countMax - this.countMin)) : this.countMin;
    }
    
    public ye getOneItem(final Random rand) {
        final mk[] items = this.getItems(rand);
        final mk item = (mk)mi.a(rand, (mj[])items);
        final ye[] stacks = generateStacks(rand, item.b, item.c, item.d);
        return (stacks.length > 0) ? stacks[0] : null;
    }
    
    public int getMin() {
        return this.countMin;
    }
    
    public int getMax() {
        return this.countMax;
    }
    
    public void setMin(final int value) {
        this.countMin = value;
    }
    
    public void setMax(final int value) {
        this.countMax = value;
    }
    
    static {
        chestInfo = new HashMap<String, ChestGenHooks>();
        ChestGenHooks.hasInit = false;
        init();
    }
}
