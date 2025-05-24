// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import cpw.mods.fml.common.FMLCommonHandler;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class EntitySpawnAdjustmentPacket extends FMLPacket
{
    public int entityId;
    public int serverX;
    public int serverY;
    public int serverZ;
    
    public EntitySpawnAdjustmentPacket() {
        super(Type.ENTITYSPAWNADJUSTMENT);
    }
    
    @Override
    public byte[] generatePacket(final Object... data) {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt((int)data[0]);
        dat.writeInt((int)data[1]);
        dat.writeInt((int)data[2]);
        dat.writeInt((int)data[3]);
        return dat.toByteArray();
    }
    
    @Override
    public FMLPacket consumePacket(final byte[] data) {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        this.entityId = dat.readInt();
        this.serverX = dat.readInt();
        this.serverY = dat.readInt();
        this.serverZ = dat.readInt();
        return this;
    }
    
    @Override
    public void execute(final cm network, final FMLNetworkHandler handler, final ez netHandler, final String userName) {
        FMLCommonHandler.instance().adjustEntityLocationOnClient(this);
    }
}
