// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import java.util.ArrayList;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.ForgeEventFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;

public class ForgeHooks
{
    static final List<GrassEntry> grassList;
    static final List<SeedEntry> seedList;
    private static boolean toolInit;
    static HashMap<yc, List> toolClasses;
    static HashMap<List, Integer> toolHarvestLevels;
    static HashSet<List> toolEffectiveness;
    
    public static void plantGrass(final abw world, final int x, final int y, final int z) {
        final GrassEntry grass = (GrassEntry)mi.a(world.s, (Collection)ForgeHooks.grassList);
        if (grass == null || grass.block == null || !grass.block.f(world, x, y, z)) {
            return;
        }
        world.f(x, y, z, grass.block.cF, grass.metadata, 3);
    }
    
    public static ye getGrassSeed(final abw world) {
        final SeedEntry entry = (SeedEntry)mi.a(world.s, (Collection)ForgeHooks.seedList);
        if (entry == null || entry.seed == null) {
            return null;
        }
        return entry.seed.m();
    }
    
    public static boolean canHarvestBlock(final aqz block, final uf player, final int metadata) {
        if (block.cU.l()) {
            return true;
        }
        final ye stack = player.bn.h();
        if (stack == null) {
            return player.a(block);
        }
        final List info = ForgeHooks.toolClasses.get(stack.b());
        if (info == null) {
            return player.a(block);
        }
        final Object[] tmp = info.toArray();
        final String toolClass = (String)tmp[0];
        final int harvestLevel = (int)tmp[1];
        final Integer blockHarvestLevel = ForgeHooks.toolHarvestLevels.get(Arrays.asList(block, metadata, toolClass));
        if (blockHarvestLevel == null) {
            return player.a(block);
        }
        return blockHarvestLevel <= harvestLevel;
    }
    
    public static boolean canToolHarvestBlock(final aqz block, final int metadata, final ye stack) {
        if (stack == null) {
            return false;
        }
        final List info = ForgeHooks.toolClasses.get(stack.b());
        if (info == null) {
            return false;
        }
        final Object[] tmp = info.toArray();
        final String toolClass = (String)tmp[0];
        final int harvestLevel = (int)tmp[1];
        final Integer blockHarvestLevel = ForgeHooks.toolHarvestLevels.get(Arrays.asList(block, metadata, toolClass));
        return blockHarvestLevel != null && blockHarvestLevel <= harvestLevel;
    }
    
    public static float blockStrength(final aqz block, final uf player, final abw world, final int x, final int y, final int z) {
        final int metadata = world.h(x, y, z);
        final float hardness = block.l(world, x, y, z);
        if (hardness < 0.0f) {
            return 0.0f;
        }
        if (!canHarvestBlock(block, player, metadata)) {
            final float speed = ForgeEventFactory.getBreakSpeed(player, block, metadata, 1.0f);
            return ((speed < 0.0f) ? 0.0f : speed) / hardness / 100.0f;
        }
        return player.getCurrentPlayerStrVsBlock(block, false, metadata) / hardness / 30.0f;
    }
    
    public static boolean isToolEffective(final ye stack, final aqz block, final int metadata) {
        final List toolClass = ForgeHooks.toolClasses.get(stack.b());
        return toolClass != null && ForgeHooks.toolEffectiveness.contains(Arrays.asList(block, metadata, toolClass.get(0)));
    }
    
    static void initTools() {
        if (ForgeHooks.toolInit) {
            return;
        }
        ForgeHooks.toolInit = true;
        MinecraftForge.setToolClass(yc.v, "pickaxe", 0);
        MinecraftForge.setToolClass(yc.z, "pickaxe", 1);
        MinecraftForge.setToolClass(yc.i, "pickaxe", 2);
        MinecraftForge.setToolClass(yc.K, "pickaxe", 0);
        MinecraftForge.setToolClass(yc.D, "pickaxe", 3);
        MinecraftForge.setToolClass(yc.w, "axe", 0);
        MinecraftForge.setToolClass(yc.A, "axe", 1);
        MinecraftForge.setToolClass(yc.j, "axe", 2);
        MinecraftForge.setToolClass(yc.L, "axe", 0);
        MinecraftForge.setToolClass(yc.E, "axe", 3);
        MinecraftForge.setToolClass(yc.u, "shovel", 0);
        MinecraftForge.setToolClass(yc.y, "shovel", 1);
        MinecraftForge.setToolClass(yc.h, "shovel", 2);
        MinecraftForge.setToolClass(yc.J, "shovel", 0);
        MinecraftForge.setToolClass(yc.C, "shovel", 3);
        for (final aqz block : yn.c) {
            MinecraftForge.setBlockHarvestLevel(block, "pickaxe", 0);
        }
        for (final aqz block : yy.c) {
            MinecraftForge.setBlockHarvestLevel(block, "shovel", 0);
        }
        for (final aqz block : ya.c) {
            MinecraftForge.setBlockHarvestLevel(block, "axe", 0);
        }
        MinecraftForge.setBlockHarvestLevel(aqz.au, "pickaxe", 3);
        MinecraftForge.setBlockHarvestLevel(aqz.bW, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(aqz.aB, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(aqz.aC, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(aqz.L, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(aqz.am, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(aqz.M, "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(aqz.an, "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(aqz.S, "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(aqz.T, "pickaxe", 1);
        MinecraftForge.setBlockHarvestLevel(aqz.aS, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(aqz.aT, "pickaxe", 2);
        MinecraftForge.removeBlockEffectiveness(aqz.aS, "pickaxe");
        MinecraftForge.removeBlockEffectiveness(aqz.au, "pickaxe");
        MinecraftForge.removeBlockEffectiveness(aqz.aT, "pickaxe");
    }
    
    public static int getTotalArmorValue(final uf player) {
        int ret = 0;
        for (int x = 0; x < player.bn.b.length; ++x) {
            final ye stack = player.bn.b[x];
            if (stack != null && stack.b() instanceof ISpecialArmor) {
                ret += ((ISpecialArmor)stack.b()).getArmorDisplay(player, stack, x);
            }
            else if (stack != null && stack.b() instanceof wh) {
                ret += ((wh)stack.b()).c;
            }
        }
        return ret;
    }
    
    public static boolean onPickBlock(final ata target, final uf player, final abw world) {
        ye result = null;
        final boolean isCreative = player.bG.d;
        if (target.a == atb.a) {
            final int x = target.b;
            final int y = target.c;
            final int z = target.d;
            final aqz var8 = aqz.s[world.a(x, y, z)];
            if (var8 == null) {
                return false;
            }
            result = var8.getPickBlock(target, world, x, y, z);
        }
        else {
            if (target.a != atb.b || target.g == null || !isCreative) {
                return false;
            }
            result = target.g.getPickedResult(target);
        }
        if (result == null) {
            return false;
        }
        for (int x = 0; x < 9; ++x) {
            final ye stack = player.bn.a(x);
            if (stack != null && stack.a(result) && ye.a(stack, result)) {
                player.bn.c = x;
                return true;
            }
        }
        if (!isCreative) {
            return false;
        }
        int slot = player.bn.j();
        if (slot < 0 || slot >= 9) {
            slot = player.bn.c;
        }
        player.bn.a(slot, result);
        player.bn.c = slot;
        return true;
    }
    
    public static void onLivingSetAttackTarget(final of entity, final of target) {
        MinecraftForge.EVENT_BUS.post(new LivingSetAttackTargetEvent(entity, target));
    }
    
    public static boolean onLivingUpdate(final of entity) {
        return MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingUpdateEvent(entity));
    }
    
    public static boolean onLivingAttack(final of entity, final nb src, final float amount) {
        return MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }
    
    public static float onLivingHurt(final of entity, final nb src, final float amount) {
        final LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
        return MinecraftForge.EVENT_BUS.post(event) ? 0.0f : event.ammount;
    }
    
    public static boolean onLivingDeath(final of entity, final nb src) {
        return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
    }
    
    public static boolean onLivingDrops(final of entity, final nb source, final ArrayList<ss> drops, final int lootingLevel, final boolean recentlyHit, final int specialDropValue) {
        return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit, specialDropValue));
    }
    
    public static float onLivingFall(final of entity, final float distance) {
        final LivingFallEvent event = new LivingFallEvent(entity, distance);
        return MinecraftForge.EVENT_BUS.post(event) ? 0.0f : event.distance;
    }
    
    public static boolean isLivingOnLadder(aqz block, final abw world, final int x, final int y, final int z, final of entity) {
        if (!ForgeDummyContainer.fullBoundingBoxLadders) {
            return block != null && block.isLadder(world, x, y, z, entity);
        }
        final asx bb = entity.E;
        final int mX = ls.c(bb.a);
        final int mY = ls.c(bb.b);
        final int mZ = ls.c(bb.c);
        for (int y2 = mY; y2 < bb.e; ++y2) {
            for (int x2 = mX; x2 < bb.d; ++x2) {
                for (int z2 = mZ; z2 < bb.f; ++z2) {
                    block = aqz.s[world.a(x2, y2, z2)];
                    if (block != null && block.isLadder(world, x2, y2, z2, entity)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static void onLivingJump(final of entity) {
        MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingJumpEvent(entity));
    }
    
    public static ss onPlayerTossEvent(final uf player, final ye item) {
        player.captureDrops = true;
        final ss ret = player.a(item, false);
        player.capturedDrops.clear();
        player.captureDrops = false;
        if (ret == null) {
            return null;
        }
        final ItemTossEvent event = new ItemTossEvent(ret, player);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return null;
        }
        player.a(event.entityItem);
        return event.entityItem;
    }
    
    public static float getEnchantPower(final abw world, final int x, final int y, final int z) {
        if (world.c(x, y, z)) {
            return 0.0f;
        }
        final aqz block = aqz.s[world.a(x, y, z)];
        return (block == null) ? 0.0f : block.getEnchantPowerBonus(world, x, y, z);
    }
    
    public static cv onServerChatEvent(final ka net, final String raw, final cv comp) {
        final ServerChatEvent event = new ServerChatEvent(net.c, raw, comp);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return null;
        }
        return event.component;
    }
    
    public static boolean canInteractWith(final uf player, final uy openContainer) {
        final PlayerOpenContainerEvent event = new PlayerOpenContainerEvent(player, openContainer);
        MinecraftForge.EVENT_BUS.post(event);
        return (event.getResult() == Event.Result.DEFAULT) ? event.canInteractWith : (event.getResult() == Event.Result.ALLOW);
    }
    
    public static BlockEvent.BreakEvent onBlockBreakEvent(final abw world, final ace gameType, final jv entityPlayer, final int x, final int y, final int z) {
        boolean preCancelEvent = false;
        if (gameType.c() && !entityPlayer.d(x, y, z)) {
            preCancelEvent = true;
        }
        else if (gameType.d() && entityPlayer.aZ() != null && entityPlayer.aZ().b() instanceof zl) {
            preCancelEvent = true;
        }
        if (world.r(x, y, z) == null) {
            final gg packet = new gg(x, y, z, world);
            packet.d = 0;
            packet.e = 0;
            entityPlayer.a.b((ey)packet);
        }
        final aqz block = aqz.s[world.a(x, y, z)];
        final int blockMetadata = world.h(x, y, z);
        final BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, block, blockMetadata, (uf)entityPlayer);
        event.setCanceled(preCancelEvent);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            entityPlayer.a.b((ey)new gg(x, y, z, world));
            final asp tileentity = world.r(x, y, z);
            if (tileentity != null) {
                final ey pkt = tileentity.m();
                if (pkt != null) {
                    entityPlayer.a.b(pkt);
                }
            }
        }
        return event;
    }
    
    static {
        grassList = new ArrayList<GrassEntry>();
        seedList = new ArrayList<SeedEntry>();
        ForgeHooks.toolInit = false;
        ForgeHooks.toolClasses = new HashMap<yc, List>();
        ForgeHooks.toolHarvestLevels = new HashMap<List, Integer>();
        ForgeHooks.toolEffectiveness = new HashSet<List>();
        ForgeHooks.grassList.add(new GrassEntry((aqz)aqz.ai, 0, 20));
        ForgeHooks.grassList.add(new GrassEntry((aqz)aqz.aj, 0, 10));
        ForgeHooks.seedList.add(new SeedEntry(new ye(yc.U), 10));
        initTools();
    }
    
    static class GrassEntry extends mj
    {
        public final aqz block;
        public final int metadata;
        
        public GrassEntry(final aqz block, final int meta, final int weight) {
            super(weight);
            this.block = block;
            this.metadata = meta;
        }
    }
    
    static class SeedEntry extends mj
    {
        public final ye seed;
        
        public SeedEntry(final ye seed, final int weight) {
            super(weight);
            this.seed = seed;
        }
    }
}
