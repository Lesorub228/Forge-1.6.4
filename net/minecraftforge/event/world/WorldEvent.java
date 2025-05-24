// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

public class WorldEvent extends Event
{
    public final abw world;
    
    public WorldEvent(final abw world) {
        this.world = world;
    }
    
    public static class Load extends WorldEvent
    {
        public Load(final abw world) {
            super(world);
        }
    }
    
    public static class Unload extends WorldEvent
    {
        public Unload(final abw world) {
            super(world);
        }
    }
    
    public static class Save extends WorldEvent
    {
        public Save(final abw world) {
            super(world);
        }
    }
    
    @Cancelable
    public static class PotentialSpawns extends WorldEvent
    {
        public final oh type;
        public final int x;
        public final int y;
        public final int z;
        public final List<acr> list;
        
        public PotentialSpawns(final abw world, final oh type, final int x, final int y, final int z, final List oldList) {
            super(world);
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
            if (oldList != null) {
                this.list = oldList;
            }
            else {
                this.list = new ArrayList<acr>();
            }
        }
    }
}
