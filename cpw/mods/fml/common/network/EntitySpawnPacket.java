// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import java.io.IOException;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.registry.EntityRegistry;
import com.google.common.io.ByteArrayDataInput;
import java.util.List;

public class EntitySpawnPacket extends FMLPacket
{
    public int networkId;
    public int modEntityId;
    public int entityId;
    public double scaledX;
    public double scaledY;
    public double scaledZ;
    public float scaledYaw;
    public float scaledPitch;
    public float scaledHeadYaw;
    public List metadata;
    public int throwerId;
    public double speedScaledX;
    public double speedScaledY;
    public double speedScaledZ;
    public ByteArrayDataInput dataStream;
    public int rawX;
    public int rawY;
    public int rawZ;
    
    public EntitySpawnPacket() {
        super(Type.ENTITYSPAWN);
    }
    
    @Override
    public byte[] generatePacket(final Object... data) {
        final EntityRegistry.EntityRegistration er = (EntityRegistry.EntityRegistration)data[0];
        final nn ent = (nn)data[1];
        final NetworkModHandler handler = (NetworkModHandler)data[2];
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt(handler.getNetworkId());
        dat.writeInt(er.getModEntityId());
        dat.writeInt(ent.k);
        dat.writeInt(ls.c(ent.u * 32.0));
        dat.writeInt(ls.c(ent.v * 32.0));
        dat.writeInt(ls.c(ent.w * 32.0));
        dat.writeByte((int)(byte)(ent.A * 256.0f / 360.0f));
        dat.writeByte((int)(byte)(ent.B * 256.0f / 360.0f));
        if (ent instanceof og) {
            dat.writeByte((int)(byte)(((og)ent).aP * 256.0f / 360.0f));
        }
        else {
            dat.writeByte(0);
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        try {
            ent.v().a((DataOutput)dos);
        }
        catch (final IOException ex) {}
        dat.write(bos.toByteArray());
        if (ent instanceof IThrowableEntity) {
            final nn owner = ((IThrowableEntity)ent).getThrower();
            dat.writeInt((owner == null) ? ent.k : owner.k);
            final double maxVel = 3.9;
            double mX = ent.x;
            double mY = ent.y;
            double mZ = ent.z;
            if (mX < -maxVel) {
                mX = -maxVel;
            }
            if (mY < -maxVel) {
                mY = -maxVel;
            }
            if (mZ < -maxVel) {
                mZ = -maxVel;
            }
            if (mX > maxVel) {
                mX = maxVel;
            }
            if (mY > maxVel) {
                mY = maxVel;
            }
            if (mZ > maxVel) {
                mZ = maxVel;
            }
            dat.writeInt((int)(mX * 8000.0));
            dat.writeInt((int)(mY * 8000.0));
            dat.writeInt((int)(mZ * 8000.0));
        }
        else {
            dat.writeInt(0);
        }
        if (ent instanceof IEntityAdditionalSpawnData) {
            ((IEntityAdditionalSpawnData)ent).writeSpawnData(dat);
        }
        return dat.toByteArray();
    }
    
    @Override
    public FMLPacket consumePacket(final byte[] data) {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        this.networkId = dat.readInt();
        this.modEntityId = dat.readInt();
        this.entityId = dat.readInt();
        this.rawX = dat.readInt();
        this.rawY = dat.readInt();
        this.rawZ = dat.readInt();
        this.scaledX = this.rawX / 32.0;
        this.scaledY = this.rawY / 32.0;
        this.scaledZ = this.rawZ / 32.0;
        this.scaledYaw = dat.readByte() * 360.0f / 256.0f;
        this.scaledPitch = dat.readByte() * 360.0f / 256.0f;
        this.scaledHeadYaw = dat.readByte() * 360.0f / 256.0f;
        final ByteArrayInputStream bis = new ByteArrayInputStream(data, 27, data.length - 27);
        final DataInputStream dis = new DataInputStream(bis);
        try {
            this.metadata = oo.a((DataInput)dis);
        }
        catch (final IOException ex) {}
        dat.skipBytes(data.length - bis.available() - 27);
        this.throwerId = dat.readInt();
        if (this.throwerId != 0) {
            this.speedScaledX = dat.readInt() / 8000.0;
            this.speedScaledY = dat.readInt() / 8000.0;
            this.speedScaledZ = dat.readInt() / 8000.0;
        }
        this.dataStream = dat;
        return this;
    }
    
    @Override
    public void execute(final cm network, final FMLNetworkHandler handler, final ez netHandler, final String userName) {
        final NetworkModHandler nmh = handler.findNetworkModHandler(this.networkId);
        final ModContainer mc = nmh.getContainer();
        final EntityRegistry.EntityRegistration registration = EntityRegistry.instance().lookupModSpawn(mc, this.modEntityId);
        if (registration == null || registration.getEntityClass() == null) {
            FMLLog.log(Level.WARNING, "Missing mod entity information for %s : %d", mc.getModId(), this.modEntityId);
            return;
        }
        final nn entity = FMLCommonHandler.instance().spawnEntityIntoClientWorld(registration, this);
    }
}
