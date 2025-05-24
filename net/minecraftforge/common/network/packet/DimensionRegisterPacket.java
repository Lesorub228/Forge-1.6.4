// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common.network.packet;

import net.minecraftforge.common.DimensionManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraftforge.common.network.ForgePacket;

public class DimensionRegisterPacket extends ForgePacket
{
    public int dimensionId;
    public int providerId;
    
    public DimensionRegisterPacket() {
    }
    
    public DimensionRegisterPacket(final int dimensionId, final int providerId) {
        this.dimensionId = dimensionId;
        this.providerId = providerId;
    }
    
    @Override
    public byte[] generatePacket() {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt(this.dimensionId);
        dat.writeInt(this.providerId);
        return dat.toByteArray();
    }
    
    @Override
    public ForgePacket consumePacket(final byte[] data) {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        this.dimensionId = dat.readInt();
        this.providerId = dat.readInt();
        return this;
    }
    
    @Override
    public void execute(final cm network, final uf player) {
        if (!(player instanceof jv) && !DimensionManager.isDimensionRegistered(this.dimensionId)) {
            DimensionManager.registerDimension(this.dimensionId, this.providerId);
        }
    }
}
