// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import com.google.common.base.Throwables;
import java.util.concurrent.ConcurrentMap;
import com.google.common.primitives.Bytes;
import java.util.Arrays;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;

public abstract class FMLPacket
{
    private Type type;
    
    public static byte[][] makePacketSet(final Type type, final Object... data) {
        if (!type.isMultipart()) {
            return new byte[0][];
        }
        final byte[] packetData = type.make().generatePacket(data);
        final byte[][] chunks = new byte[packetData.length / 32000 + 1][];
        for (int i = 0; i < packetData.length / 32000 + 1; ++i) {
            final int len = Math.min(32000, packetData.length - i * 32000);
            chunks[i] = Bytes.concat(new byte[][] { { UnsignedBytes.checkedCast((long)type.ordinal()), UnsignedBytes.checkedCast((long)i), UnsignedBytes.checkedCast((long)chunks.length) }, Ints.toByteArray(len), Arrays.copyOfRange(packetData, i * 32000, len + i * 32000) });
        }
        return chunks;
    }
    
    public static byte[] makePacket(final Type type, final Object... data) {
        final byte[] packetData = type.make().generatePacket(data);
        return Bytes.concat(new byte[][] { { UnsignedBytes.checkedCast((long)type.ordinal()) }, packetData });
    }
    
    public static FMLPacket readPacket(final cm network, final byte[] payload) {
        final int type = UnsignedBytes.toInt(payload[0]);
        final Type eType = Type.values()[type];
        FMLPacket pkt;
        if (eType.isMultipart()) {
            pkt = eType.findCurrentPart(network);
        }
        else {
            pkt = eType.make();
        }
        return pkt.consumePacket(Arrays.copyOfRange(payload, 1, payload.length));
    }
    
    public FMLPacket(final Type type) {
        this.type = type;
    }
    
    public abstract byte[] generatePacket(final Object... p0);
    
    public abstract FMLPacket consumePacket(final byte[] p0);
    
    public abstract void execute(final cm p0, final FMLNetworkHandler p1, final ez p2, final String p3);
    
    enum Type
    {
        MOD_LIST_REQUEST((Class<? extends FMLPacket>)ModListRequestPacket.class, false), 
        MOD_LIST_RESPONSE((Class<? extends FMLPacket>)ModListResponsePacket.class, false), 
        MOD_IDENTIFIERS((Class<? extends FMLPacket>)ModIdentifiersPacket.class, false), 
        MOD_MISSING((Class<? extends FMLPacket>)ModMissingPacket.class, false), 
        GUIOPEN((Class<? extends FMLPacket>)OpenGuiPacket.class, false), 
        ENTITYSPAWN((Class<? extends FMLPacket>)EntitySpawnPacket.class, false), 
        ENTITYSPAWNADJUSTMENT((Class<? extends FMLPacket>)EntitySpawnAdjustmentPacket.class, false), 
        MOD_IDMAP((Class<? extends FMLPacket>)ModIdMapPacket.class, true);
        
        private Class<? extends FMLPacket> packetType;
        private boolean isMultipart;
        private ConcurrentMap<cm, FMLPacket> partTracker;
        
        private Type(final Class<? extends FMLPacket> clazz, final boolean isMultipart) {
            this.packetType = clazz;
            this.isMultipart = isMultipart;
        }
        
        FMLPacket make() {
            try {
                return (FMLPacket)this.packetType.newInstance();
            }
            catch (final Exception e) {
                Throwables.propagateIfPossible((Throwable)e);
                FMLLog.log(Level.SEVERE, e, "A bizarre critical error occured during packet encoding", new Object[0]);
                throw new FMLNetworkException(e);
            }
        }
        
        public boolean isMultipart() {
            return this.isMultipart;
        }
        
        private FMLPacket findCurrentPart(final cm network) {
            if (this.partTracker == null) {
                this.partTracker = new MapMaker().weakKeys().weakValues().makeMap();
            }
            if (!this.partTracker.containsKey(network)) {
                this.partTracker.put(network, this.make());
            }
            return this.partTracker.get(network);
        }
    }
}
