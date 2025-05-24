// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event;

import net.minecraftforge.event.entity.living.ZombieEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import java.util.ArrayList;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.world.WorldEvent;
import java.util.List;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ForgeEventFactory
{
    public static boolean doPlayerHarvestCheck(final uf player, final aqz block, final boolean success) {
        final PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, block, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.success;
    }
    
    public static float getBreakSpeed(final uf player, final aqz block, final int metadata, final float original) {
        final PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, block, metadata, original);
        return MinecraftForge.EVENT_BUS.post(event) ? -1.0f : event.newSpeed;
    }
    
    public static PlayerInteractEvent onPlayerInteract(final uf player, final PlayerInteractEvent.Action action, final int x, final int y, final int z, final int face) {
        final PlayerInteractEvent event = new PlayerInteractEvent(player, action, x, y, z, face);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }
    
    public static void onPlayerDestroyItem(final uf player, final ye stack) {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack));
    }
    
    public static Event.Result canEntitySpawn(final og entity, final abw world, final float x, final float y, final float z) {
        final LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }
    
    public static boolean doSpecialSpawn(final og entity, final abw world, final float x, final float y, final float z) {
        return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z));
    }
    
    public static Event.Result canEntityDespawn(final og entity) {
        final LivingSpawnEvent.AllowDespawn event = new LivingSpawnEvent.AllowDespawn(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }
    
    public static List getPotentialSpawns(final js world, final oh type, final int x, final int y, final int z, final List oldList) {
        final WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns((abw)world, type, x, y, z, oldList);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return null;
        }
        return event.list;
    }
    
    public static int getMaxSpawnPackSize(final og entity) {
        final LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        MinecraftForge.EVENT_BUS.post(maxCanSpawnEvent);
        return (maxCanSpawnEvent.getResult() == Event.Result.ALLOW) ? maxCanSpawnEvent.maxPackSize : entity.bv();
    }
    
    public static String getPlayerDisplayName(final uf player, final String username) {
        final PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        MinecraftForge.EVENT_BUS.post(event);
        return event.displayname;
    }
    
    public static float fireBlockHarvesting(final ArrayList<ye> drops, final abw world, final aqz block, final int x, final int y, final int z, final int meta, final int fortune, final float dropChance, final boolean silkTouch, final uf player) {
        final BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(x, y, z, world, block, meta, fortune, dropChance, drops, player, silkTouch);
        MinecraftForge.EVENT_BUS.post(event);
        return event.dropChance;
    }
    
    public static ItemTooltipEvent onItemTooltip(final ye itemStack, final uf entityPlayer, final List<String> toolTip, final boolean showAdvancedItemTooltips) {
        final ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, toolTip, showAdvancedItemTooltips);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }
    
    public static ZombieEvent.SummonAidEvent fireZombieSummonAid(final tw zombie, final abw world, final int x, final int y, final int z, final of attacker, final double summonChance) {
        final ZombieEvent.SummonAidEvent summonEvent = new ZombieEvent.SummonAidEvent(zombie, world, x, y, z, attacker, summonChance);
        MinecraftForge.EVENT_BUS.post(summonEvent);
        return summonEvent;
    }
}
