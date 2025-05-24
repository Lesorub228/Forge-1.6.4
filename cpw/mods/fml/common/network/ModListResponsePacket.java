// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Logger;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Loader;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.List;
import java.util.Map;

public class ModListResponsePacket extends FMLPacket
{
    private Map<String, String> modVersions;
    private List<String> missingMods;
    
    public ModListResponsePacket() {
        super(Type.MOD_LIST_RESPONSE);
    }
    
    @Override
    public byte[] generatePacket(final Object... data) {
        final Map<String, String> modVersions = (Map<String, String>)data[0];
        final List<String> missingMods = (List<String>)data[1];
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt(modVersions.size());
        for (final Map.Entry<String, String> version : modVersions.entrySet()) {
            dat.writeUTF((String)version.getKey());
            dat.writeUTF((String)version.getValue());
        }
        dat.writeInt(missingMods.size());
        for (final String missing : missingMods) {
            dat.writeUTF(missing);
        }
        return dat.toByteArray();
    }
    
    @Override
    public FMLPacket consumePacket(final byte[] data) {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        final int versionListSize = dat.readInt();
        this.modVersions = Maps.newHashMapWithExpectedSize(versionListSize);
        for (int i = 0; i < versionListSize; ++i) {
            final String modName = dat.readUTF();
            final String modVersion = dat.readUTF();
            this.modVersions.put(modName, modVersion);
        }
        final int missingModSize = dat.readInt();
        this.missingMods = Lists.newArrayListWithExpectedSize(missingModSize);
        for (int j = 0; j < missingModSize; ++j) {
            this.missingMods.add(dat.readUTF());
        }
        return this;
    }
    
    @Override
    public void execute(final cm network, final FMLNetworkHandler handler, final ez netHandler, final String userName) {
        final Map<String, ModContainer> indexedModList = Maps.newHashMap((Map)Loader.instance().getIndexedModList());
        final List<String> missingClientMods = Lists.newArrayList();
        final List<String> versionIncorrectMods = Lists.newArrayList();
        for (final String m : this.missingMods) {
            final ModContainer mc = indexedModList.get(m);
            final NetworkModHandler networkMod = handler.findNetworkModHandler(mc);
            if (networkMod.requiresClientSide()) {
                missingClientMods.add(m);
            }
        }
        for (final Map.Entry<String, String> modVersion : this.modVersions.entrySet()) {
            final ModContainer mc = indexedModList.get(modVersion.getKey());
            final NetworkModHandler networkMod = handler.findNetworkModHandler(mc);
            if (!networkMod.acceptVersion(modVersion.getValue())) {
                versionIncorrectMods.add(modVersion.getKey());
            }
        }
        final ea pkt = new ea();
        pkt.a = "FML";
        if (missingClientMods.size() > 0 || versionIncorrectMods.size() > 0) {
            pkt.c = FMLPacket.makePacket(Type.MOD_MISSING, missingClientMods, versionIncorrectMods);
            Logger.getLogger("Minecraft").info(String.format("User %s connection failed: missing %s, bad versions %s", userName, missingClientMods, versionIncorrectMods));
            FMLLog.info("User %s connection failed: missing %s, bad versions %s", userName, missingClientMods, versionIncorrectMods);
            FMLNetworkHandler.setHandlerState((jy)netHandler, -2);
            pkt.b = pkt.c.length;
            network.a((ey)pkt);
        }
        else {
            pkt.c = FMLPacket.makePacket(Type.MOD_IDENTIFIERS, netHandler);
            Logger.getLogger("Minecraft").info(String.format("User %s connecting with mods %s", userName, this.modVersions.keySet()));
            FMLLog.info("User %s connecting with mods %s", userName, this.modVersions.keySet());
            pkt.b = pkt.c.length;
            network.a((ey)pkt);
            final cg itemList = new cg();
            GameData.writeItemData(itemList);
            final byte[][] registryPackets = FMLPacket.makePacketSet(Type.MOD_IDMAP, itemList);
            for (int i = 0; i < registryPackets.length; ++i) {
                network.a((ey)PacketDispatcher.getPacket("FML", registryPackets[i]));
            }
        }
        jy.a((jy)netHandler, true);
    }
}
