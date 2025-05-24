// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class OpenGuiPacket extends FMLPacket
{
    private int windowId;
    private int networkId;
    private int modGuiId;
    private int x;
    private int y;
    private int z;
    
    public OpenGuiPacket() {
        super(Type.GUIOPEN);
    }
    
    @Override
    public byte[] generatePacket(final Object... data) {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt((int)data[0]);
        dat.writeInt((int)data[1]);
        dat.writeInt((int)data[2]);
        dat.writeInt((int)data[3]);
        dat.writeInt((int)data[4]);
        dat.writeInt((int)data[5]);
        return dat.toByteArray();
    }
    
    @Override
    public FMLPacket consumePacket(final byte[] data) {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        this.windowId = dat.readInt();
        this.networkId = dat.readInt();
        this.modGuiId = dat.readInt();
        this.x = dat.readInt();
        this.y = dat.readInt();
        this.z = dat.readInt();
        return this;
    }
    
    @Override
    public void execute(final cm network, final FMLNetworkHandler handler, final ez netHandler, final String userName) {
        final uf player = netHandler.getPlayer();
        player.openGui((Object)this.networkId, this.modGuiId, player.q, this.x, this.y, this.z);
        player.bp.d = this.windowId;
    }
}
