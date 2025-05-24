// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.Collection;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;

public class DungeonHooks
{
    private static ArrayList<DungeonMob> dungeonMobs;
    
    public static float addDungeonMob(final String name, final int rarity) {
        if (rarity <= 0) {
            throw new IllegalArgumentException("Rarity must be greater then zero");
        }
        for (final DungeonMob mob : DungeonHooks.dungeonMobs) {
            if (name.equals(mob.type)) {
                final DungeonMob dungeonMob = mob;
                final int a = dungeonMob.a + rarity;
                dungeonMob.a = a;
                return (float)a;
            }
        }
        DungeonHooks.dungeonMobs.add(new DungeonMob(rarity, name));
        return (float)rarity;
    }
    
    public static int removeDungeonMob(final String name) {
        for (final DungeonMob mob : DungeonHooks.dungeonMobs) {
            if (name.equals(mob.type)) {
                DungeonHooks.dungeonMobs.remove(mob);
                return mob.a;
            }
        }
        return 0;
    }
    
    public static String getRandomDungeonMob(final Random rand) {
        final DungeonMob mob = (DungeonMob)mi.a(rand, (Collection)DungeonHooks.dungeonMobs);
        if (mob == null) {
            return "";
        }
        return mob.type;
    }
    
    static {
        DungeonHooks.dungeonMobs = new ArrayList<DungeonMob>();
        addDungeonMob("Skeleton", 100);
        addDungeonMob("Zombie", 200);
        addDungeonMob("Spider", 100);
    }
    
    public static class DungeonMob extends mj
    {
        public String type;
        
        public DungeonMob(final int weight, final String type) {
            super(weight);
            this.type = type;
        }
        
        public boolean equals(final Object target) {
            return target instanceof DungeonMob && this.type.equals(((DungeonMob)target).type);
        }
    }
}
