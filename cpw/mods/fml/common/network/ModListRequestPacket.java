// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import cpw.mods.fml.common.FMLCommonHandler;
import java.util.Map;
import cpw.mods.fml.common.Loader;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import cpw.mods.fml.common.FMLLog;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.Set;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.ModContainer;
import com.google.common.io.ByteStreams;
import java.util.List;

public class ModListRequestPacket extends FMLPacket
{
    private List<String> sentModList;
    private byte compatibilityLevel;
    
    public ModListRequestPacket() {
        super(Type.MOD_LIST_REQUEST);
    }
    
    @Override
    public byte[] generatePacket(final Object... data) {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        final Set<ModContainer> activeMods = FMLNetworkHandler.instance().getNetworkModList();
        dat.writeInt(activeMods.size());
        for (final ModContainer mc : activeMods) {
            dat.writeUTF(mc.getModId());
        }
        dat.writeByte(FMLNetworkHandler.getCompatibilityLevel());
        return dat.toByteArray();
    }
    
    @Override
    public FMLPacket consumePacket(final byte[] data) {
        this.sentModList = Lists.newArrayList();
        final ByteArrayDataInput in = ByteStreams.newDataInput(data);
        for (int listSize = in.readInt(), i = 0; i < listSize; ++i) {
            this.sentModList.add(in.readUTF());
        }
        try {
            this.compatibilityLevel = in.readByte();
        }
        catch (final IllegalStateException e) {
            FMLLog.fine("No compatibility byte found - the server is too old", new Object[0]);
        }
        return this;
    }
    
    @Override
    public void execute(final cm mgr, final FMLNetworkHandler handler, final ez netHandler, final String userName) {
        final List<String> missingMods = Lists.newArrayList();
        final Map<String, String> modVersions = Maps.newHashMap();
        final Map<String, ModContainer> indexedModList = Maps.newHashMap((Map)Loader.instance().getIndexedModList());
        for (final String m : this.sentModList) {
            final ModContainer mc = indexedModList.get(m);
            if (mc == null) {
                missingMods.add(m);
            }
            else {
                indexedModList.remove(m);
                modVersions.put(m, mc.getVersion());
            }
        }
        if (indexedModList.size() > 0) {
            for (final Map.Entry<String, ModContainer> e : indexedModList.entrySet()) {
                if (e.getValue().isNetworkMod()) {
                    final NetworkModHandler missingHandler = FMLNetworkHandler.instance().findNetworkModHandler(e.getValue());
                    if (!missingHandler.requiresServerSide()) {
                        continue;
                    }
                    FMLLog.warning("The mod %s was not found on the server you connected to, but requested that the server side be present", e.getKey());
                }
            }
        }
        FMLLog.fine("The server has compatibility level %d", this.compatibilityLevel);
        FMLCommonHandler.instance().getSidedDelegate().setClientCompatibilityLevel(this.compatibilityLevel);
        mgr.a((ey)PacketDispatcher.getPacket("FML", FMLPacket.makePacket(Type.MOD_LIST_RESPONSE, modVersions, missingMods)));
    }
}
