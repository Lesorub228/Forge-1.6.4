// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import com.google.common.io.ByteArrayDataInput;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.collect.Maps;
import java.util.Map;

public class ModIdentifiersPacket extends FMLPacket
{
    private Map<String, Integer> modIds;
    
    public ModIdentifiersPacket() {
        super(Type.MOD_IDENTIFIERS);
        this.modIds = Maps.newHashMap();
    }
    
    @Override
    public byte[] generatePacket(final Object... data) {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        final Collection<NetworkModHandler> networkMods = FMLNetworkHandler.instance().getNetworkIdMap().values();
        dat.writeInt(networkMods.size());
        for (final NetworkModHandler handler : networkMods) {
            dat.writeUTF(handler.getContainer().getModId());
            dat.writeInt(handler.getNetworkId());
        }
        return dat.toByteArray();
    }
    
    @Override
    public FMLPacket consumePacket(final byte[] data) {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        for (int listSize = dat.readInt(), i = 0; i < listSize; ++i) {
            final String modId = dat.readUTF();
            final int networkId = dat.readInt();
            this.modIds.put(modId, networkId);
        }
        return this;
    }
    
    @Override
    public void execute(final cm network, final FMLNetworkHandler handler, final ez netHandler, final String userName) {
        for (final Map.Entry<String, Integer> idEntry : this.modIds.entrySet()) {
            handler.bindNetworkId(idEntry.getKey(), idEntry.getValue());
        }
    }
}
