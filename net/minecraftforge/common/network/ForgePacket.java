// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common.network;

import net.minecraftforge.fluids.FluidIdMapPacket;
import net.minecraftforge.common.network.packet.DimensionRegisterPacket;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.network.FMLNetworkException;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import com.google.common.base.Throwables;
import java.util.concurrent.ConcurrentMap;
import java.util.Arrays;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedBytes;

public abstract class ForgePacket
{
    public static final String CHANNEL_ID = "FORGE";
    private Type type;
    private byte[][] partials;
    
    public static ea[] makePacketSet(final ForgePacket packet) {
        final byte[] packetData = packet.generatePacket();
        if (packetData.length < 32000) {
            return new ea[] { new ea("FORGE", Bytes.concat(new byte[][] { { UnsignedBytes.checkedCast(0L), UnsignedBytes.checkedCast((long)packet.getID()) }, packetData })) };
        }
        final byte[][] chunks = new byte[packetData.length / 32000 + 1][];
        for (int i = 0; i < packetData.length / 32000 + 1; ++i) {
            final int len = Math.min(32000, packetData.length - i * 32000);
            chunks[i] = Bytes.concat(new byte[][] { { UnsignedBytes.checkedCast(1L), UnsignedBytes.checkedCast((long)packet.getID()), UnsignedBytes.checkedCast((long)i), UnsignedBytes.checkedCast((long)chunks.length) }, Ints.toByteArray(len), Arrays.copyOfRange(packetData, i * 32000, len + i * 32000) });
        }
        final ea[] ret = new ea[chunks.length];
        for (int j = 0; j < chunks.length; ++j) {
            ret[j] = new ea("FORGE", chunks[j]);
        }
        return ret;
    }
    
    public static ForgePacket readPacket(final cm network, final byte[] payload) {
        final boolean multipart = UnsignedBytes.toInt(payload[0]) == 1;
        final int type = UnsignedBytes.toInt(payload[1]);
        final Type eType = Type.values()[type];
        final byte[] data = Arrays.copyOfRange(payload, 2, payload.length);
        if (!multipart) {
            return eType.make().consumePacket(data);
        }
        final ForgePacket pkt = eType.consumePart(network, data);
        if (pkt != null) {
            return pkt.consumePacket(Bytes.concat(pkt.partials));
        }
        return null;
    }
    
    public ForgePacket() {
        for (final Type t : Type.values()) {
            if (t.packetType == this.getClass()) {
                this.type = t;
            }
        }
        if (this.type == null) {
            throw new RuntimeException("ForgePacket constructor called on ungregistered type.");
        }
    }
    
    public byte getID() {
        return UnsignedBytes.checkedCast((long)this.type.ordinal());
    }
    
    public abstract byte[] generatePacket();
    
    public abstract ForgePacket consumePacket(final byte[] p0);
    
    public abstract void execute(final cm p0, final uf p1);
    
    enum Type
    {
        REGISTERDIMENSION((Class<? extends ForgePacket>)DimensionRegisterPacket.class), 
        FLUID_IDMAP((Class<? extends ForgePacket>)FluidIdMapPacket.class);
        
        private Class<? extends ForgePacket> packetType;
        private ConcurrentMap<cm, ForgePacket> partTracker;
        
        private Type(final Class<? extends ForgePacket> clazz) {
            this.packetType = clazz;
        }
        
        ForgePacket make() {
            try {
                return (ForgePacket)this.packetType.newInstance();
            }
            catch (final Exception e) {
                Throwables.propagateIfPossible((Throwable)e);
                FMLLog.log(Level.SEVERE, e, "A bizarre critical error occured during packet encoding", new Object[0]);
                throw new FMLNetworkException(e);
            }
        }
        
        private ForgePacket consumePart(final cm network, final byte[] data) {
            if (this.partTracker == null) {
                this.partTracker = new MapMaker().weakKeys().weakValues().makeMap();
            }
            if (!this.partTracker.containsKey(network)) {
                this.partTracker.put(network, this.make());
            }
            final ForgePacket pkt = this.partTracker.get(network);
            final ByteArrayDataInput bdi = ByteStreams.newDataInput(data);
            final int chunkIdx = UnsignedBytes.toInt(bdi.readByte());
            final int chunkTotal = UnsignedBytes.toInt(bdi.readByte());
            final int chunkLength = bdi.readInt();
            if (pkt.partials == null) {
                pkt.partials = new byte[chunkTotal][];
            }
            bdi.readFully(pkt.partials[chunkIdx] = new byte[chunkLength]);
            for (int i = 0; i < pkt.partials.length; ++i) {
                if (pkt.partials[i] == null) {
                    return null;
                }
            }
            return pkt;
        }
    }
}
