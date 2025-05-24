// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import com.google.common.io.ByteArrayDataInput;
import java.util.Iterator;
import com.google.common.io.ByteArrayDataOutput;
import java.util.Map;
import com.google.common.io.ByteStreams;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.BiMap;
import net.minecraftforge.common.network.ForgePacket;

public class FluidIdMapPacket extends ForgePacket
{
    private BiMap<String, Integer> fluidIds;
    
    public FluidIdMapPacket() {
        this.fluidIds = (BiMap<String, Integer>)HashBiMap.create();
    }
    
    @Override
    public byte[] generatePacket() {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt(FluidRegistry.maxID);
        for (final Map.Entry<String, Integer> entry : FluidRegistry.fluidIDs.entrySet()) {
            dat.writeUTF((String)entry.getKey());
            dat.writeInt((int)entry.getValue());
        }
        return dat.toByteArray();
    }
    
    @Override
    public ForgePacket consumePacket(final byte[] data) {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        for (int listSize = dat.readInt(), i = 0; i < listSize; ++i) {
            final String fluidName = dat.readUTF();
            final int fluidId = dat.readInt();
            this.fluidIds.put((Object)fluidName, (Object)fluidId);
        }
        return this;
    }
    
    @Override
    public void execute(final cm network, final uf player) {
        FluidRegistry.initFluidIDs(this.fluidIds);
    }
}
