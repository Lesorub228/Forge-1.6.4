// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class ForgeInternalHandler
{
    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        if (!event.world.I) {
            ForgeChunkManager.loadEntity(event.entity);
        }
        final nn entity = event.entity;
        if (entity.getClass().equals(ss.class)) {
            final ye stack = entity.v().f(10);
            if (stack == null) {
                return;
            }
            final yc item = stack.b();
            if (item == null) {
                FMLLog.warning("Attempted to add a EntityItem to the world with a invalid item: ID %d at (%2.2f,  %2.2f, %2.2f), this is most likely a config issue between you and the server. Please double check your configs", stack.d, entity.u, entity.v, entity.w);
                entity.x();
                event.setCanceled(true);
                return;
            }
            if (item.hasCustomEntity(stack)) {
                final nn newEntity = item.createEntity(event.world, entity, stack);
                if (newEntity != null) {
                    entity.x();
                    event.setCanceled(true);
                    event.world.d(newEntity);
                }
            }
        }
    }
    
    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onDimensionLoad(final WorldEvent.Load event) {
        ForgeChunkManager.loadWorld(event.world);
    }
    
    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onDimensionSave(final WorldEvent.Save event) {
        ForgeChunkManager.saveWorld(event.world);
    }
    
    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onDimensionUnload(final WorldEvent.Unload event) {
        ForgeChunkManager.unloadWorld(event.world);
    }
}
