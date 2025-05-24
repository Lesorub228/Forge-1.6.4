// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.Arrays;
import java.util.ArrayList;

public interface ISpecialArmor
{
    ArmorProperties getProperties(final of p0, final ye p1, final nb p2, final double p3, final int p4);
    
    int getArmorDisplay(final uf p0, final ye p1, final int p2);
    
    void damageArmor(final of p0, final ye p1, final nb p2, final int p3, final int p4);
    
    public static class ArmorProperties implements Comparable<ArmorProperties>
    {
        public int Priority;
        public int AbsorbMax;
        public double AbsorbRatio;
        public int Slot;
        private static final boolean DEBUG = false;
        
        public ArmorProperties(final int priority, final double ratio, final int max) {
            this.Priority = 0;
            this.AbsorbMax = Integer.MAX_VALUE;
            this.AbsorbRatio = 0.0;
            this.Slot = 0;
            this.Priority = priority;
            this.AbsorbRatio = ratio;
            this.AbsorbMax = max;
        }
        
        public static float ApplyArmor(final of entity, final ye[] inventory, final nb source, double damage) {
            damage *= 25.0;
            final ArrayList<ArmorProperties> dmgVals = new ArrayList<ArmorProperties>();
            for (int x = 0; x < inventory.length; ++x) {
                final ye stack = inventory[x];
                if (stack != null) {
                    ArmorProperties prop = null;
                    if (stack.b() instanceof ISpecialArmor) {
                        final ISpecialArmor armor = (ISpecialArmor)stack.b();
                        prop = armor.getProperties(entity, stack, source, damage / 25.0, x).copy();
                    }
                    else if (stack.b() instanceof wh && !source.e()) {
                        final wh armor2 = (wh)stack.b();
                        prop = new ArmorProperties(0, armor2.c / 25.0, armor2.o() + 1 - stack.k());
                    }
                    if (prop != null) {
                        prop.Slot = x;
                        dmgVals.add(prop);
                    }
                }
            }
            if (dmgVals.size() > 0) {
                final ArmorProperties[] props = dmgVals.toArray(new ArmorProperties[dmgVals.size()]);
                StandardizeList(props, damage);
                int level = props[0].Priority;
                double ratio = 0.0;
                for (final ArmorProperties prop2 : props) {
                    if (level != prop2.Priority) {
                        damage -= damage * ratio;
                        ratio = 0.0;
                        level = prop2.Priority;
                    }
                    ratio += prop2.AbsorbRatio;
                    final double absorb = damage * prop2.AbsorbRatio;
                    if (absorb > 0.0) {
                        final ye stack2 = inventory[prop2.Slot];
                        final int itemDamage = (int)((absorb / 25.0 < 1.0) ? 1.0 : (absorb / 25.0));
                        if (stack2.b() instanceof ISpecialArmor) {
                            ((ISpecialArmor)stack2.b()).damageArmor(entity, stack2, source, itemDamage, prop2.Slot);
                        }
                        else {
                            stack2.a(itemDamage, entity);
                        }
                        if (stack2.b <= 0) {
                            inventory[prop2.Slot] = null;
                        }
                    }
                }
                damage -= damage * ratio;
            }
            return (float)(damage / 25.0);
        }
        
        private static void StandardizeList(final ArmorProperties[] armor, double damage) {
            Arrays.sort(armor);
            int start = 0;
            double total = 0.0;
            int priority = armor[0].Priority;
            int pStart = 0;
            boolean pChange = false;
            boolean pFinished = false;
            for (int x = 0; x < armor.length; ++x) {
                total += armor[x].AbsorbRatio;
                if (x == armor.length - 1 || armor[x].Priority != priority) {
                    if (armor[x].Priority != priority) {
                        total -= armor[x].AbsorbRatio;
                        --x;
                        pChange = true;
                    }
                    if (total > 1.0) {
                        for (int y = start; y <= x; ++y) {
                            final double newRatio = armor[y].AbsorbRatio / total;
                            if (newRatio * damage > armor[y].AbsorbMax) {
                                armor[y].AbsorbRatio = armor[y].AbsorbMax / damage;
                                total = 0.0;
                                for (int z = pStart; z <= y; ++z) {
                                    total += armor[z].AbsorbRatio;
                                }
                                start = y + 1;
                                x = y;
                                break;
                            }
                            armor[y].AbsorbRatio = newRatio;
                            pFinished = true;
                        }
                        if (pChange && pFinished) {
                            damage -= damage * total;
                            total = 0.0;
                            start = x + 1;
                            priority = armor[start].Priority;
                            pStart = start;
                            pChange = false;
                            pFinished = false;
                            if (damage <= 0.0) {
                                for (int y = x + 1; y < armor.length; ++y) {
                                    armor[y].AbsorbRatio = 0.0;
                                }
                                break;
                            }
                        }
                    }
                    else {
                        for (int y = start; y <= x; ++y) {
                            total -= armor[y].AbsorbRatio;
                            if (damage * armor[y].AbsorbRatio > armor[y].AbsorbMax) {
                                armor[y].AbsorbRatio = armor[y].AbsorbMax / damage;
                            }
                            total += armor[y].AbsorbRatio;
                        }
                        damage -= damage * total;
                        total = 0.0;
                        if (x != armor.length - 1) {
                            start = x + 1;
                            priority = armor[start].Priority;
                            pStart = start;
                            pChange = false;
                            if (damage <= 0.0) {
                                for (int y = x + 1; y < armor.length; ++y) {
                                    armor[y].AbsorbRatio = 0.0;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        @Override
        public int compareTo(final ArmorProperties o) {
            if (o.Priority != this.Priority) {
                return o.Priority - this.Priority;
            }
            final double left = (this.AbsorbRatio == 0.0) ? 0.0 : (this.AbsorbMax * 100.0 / this.AbsorbRatio);
            final double right = (o.AbsorbRatio == 0.0) ? 0.0 : (o.AbsorbMax * 100.0 / o.AbsorbRatio);
            return (int)(left - right);
        }
        
        @Override
        public String toString() {
            return String.format("%d, %d, %f, %d", this.Priority, this.AbsorbMax, this.AbsorbRatio, (this.AbsorbRatio == 0.0) ? 0 : ((int)(this.AbsorbMax * 100.0 / this.AbsorbRatio)));
        }
        
        public ArmorProperties copy() {
            return new ArmorProperties(this.Priority, this.AbsorbRatio, this.AbsorbMax);
        }
    }
}
