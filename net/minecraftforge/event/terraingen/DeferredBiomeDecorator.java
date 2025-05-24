// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import net.minecraftforge.event.Event;
import net.minecraftforge.common.MinecraftForge;
import java.util.Random;

public class DeferredBiomeDecorator extends acu
{
    private acu wrapped;
    
    public DeferredBiomeDecorator(final acq biomeGenBase, final acu wrappedOriginal) {
        super(biomeGenBase);
        this.wrapped = wrappedOriginal;
    }
    
    public void a(final abw par1World, final Random par2Random, final int par3, final int par4) {
        this.fireCreateEventAndReplace();
        this.e.I.a(par1World, par2Random, par3, par4);
    }
    
    public void fireCreateEventAndReplace() {
        this.wrapped.J = this.J;
        this.wrapped.F = this.F;
        this.wrapped.I = this.I;
        this.wrapped.C = this.C;
        this.wrapped.A = this.A;
        this.wrapped.K = this.K;
        this.wrapped.B = this.B;
        this.wrapped.D = this.D;
        this.wrapped.E = this.E;
        this.wrapped.G = this.G;
        this.wrapped.H = this.H;
        this.wrapped.z = this.z;
        this.wrapped.y = this.y;
        final BiomeEvent.CreateDecorator event = new BiomeEvent.CreateDecorator(this.e, this.wrapped);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        this.e.I = event.newBiomeDecorator;
    }
}
