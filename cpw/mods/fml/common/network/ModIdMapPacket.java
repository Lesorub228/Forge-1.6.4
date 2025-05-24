// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import com.google.common.collect.MapDifference;
import cpw.mods.fml.common.registry.ItemData;
import java.util.Set;
import java.io.IOException;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameData;
import com.google.common.primitives.Bytes;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.primitives.UnsignedBytes;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;

public class ModIdMapPacket extends FMLPacket
{
    private byte[][] partials;
    
    public ModIdMapPacket() {
        super(Type.MOD_IDMAP);
    }
    
    @Override
    public byte[] generatePacket(final Object... data) {
        final cg completeList = (cg)data[0];
        final by wrap = new by();
        wrap.a("List", (cl)completeList);
        try {
            return ci.a(wrap);
        }
        catch (final Exception e) {
            FMLLog.log(Level.SEVERE, e, "A critical error writing the id map", new Object[0]);
            throw new FMLNetworkException(e);
        }
    }
    
    @Override
    public FMLPacket consumePacket(final byte[] data) {
        final ByteArrayDataInput bdi = ByteStreams.newDataInput(data);
        final int chunkIdx = UnsignedBytes.toInt(bdi.readByte());
        final int chunkTotal = UnsignedBytes.toInt(bdi.readByte());
        final int chunkLength = bdi.readInt();
        if (this.partials == null) {
            this.partials = new byte[chunkTotal][];
        }
        bdi.readFully(this.partials[chunkIdx] = new byte[chunkLength]);
        for (int i = 0; i < this.partials.length; ++i) {
            if (this.partials[i] == null) {
                return null;
            }
        }
        return this;
    }
    
    @Override
    public void execute(final cm network, final FMLNetworkHandler handler, final ez netHandler, final String userName) {
        final byte[] allData = Bytes.concat(this.partials);
        GameData.initializeServerGate(1);
        try {
            final by serverList = ci.a(allData);
            final cg list = serverList.m("List");
            final Set<ItemData> itemData = GameData.buildWorldItemData(list);
            GameData.validateWorldSave(itemData);
            final MapDifference<Integer, ItemData> serverDifference = GameData.gateWorldLoadingForValidation();
            if (serverDifference != null) {
                FMLCommonHandler.instance().disconnectIDMismatch(serverDifference, netHandler, network);
            }
        }
        catch (final IOException ex) {}
    }
}
