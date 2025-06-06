// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.FMLCommonHandler;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.collect.Lists;
import java.util.Iterator;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import com.google.common.io.ByteStreams;
import java.util.List;

public class ModMissingPacket extends FMLPacket
{
    private List<ModData> missing;
    private List<ModData> badVersion;
    
    public ModMissingPacket() {
        super(Type.MOD_MISSING);
    }
    
    @Override
    public byte[] generatePacket(final Object... data) {
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        final List<String> missing = (List<String>)data[0];
        final List<String> badVersion = (List<String>)data[1];
        dat.writeInt(missing.size());
        for (final String missed : missing) {
            final ModContainer mc = Loader.instance().getIndexedModList().get(missed);
            dat.writeUTF(missed);
            dat.writeUTF(mc.getVersion());
        }
        dat.writeInt(badVersion.size());
        for (final String bad : badVersion) {
            final ModContainer mc = Loader.instance().getIndexedModList().get(bad);
            dat.writeUTF(bad);
            dat.writeUTF(mc.getVersion());
        }
        return dat.toByteArray();
    }
    
    @Override
    public FMLPacket consumePacket(final byte[] data) {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        final int missingLen = dat.readInt();
        this.missing = Lists.newArrayListWithCapacity(missingLen);
        for (int i = 0; i < missingLen; ++i) {
            final ModData md = new ModData();
            md.modId = dat.readUTF();
            md.modVersion = dat.readUTF();
            this.missing.add(md);
        }
        final int badVerLength = dat.readInt();
        this.badVersion = Lists.newArrayListWithCapacity(badVerLength);
        for (int j = 0; j < badVerLength; ++j) {
            final ModData md2 = new ModData();
            md2.modId = dat.readUTF();
            md2.modVersion = dat.readUTF();
            this.badVersion.add(md2);
        }
        return this;
    }
    
    @Override
    public void execute(final cm network, final FMLNetworkHandler handler, final ez netHandler, final String userName) {
        FMLCommonHandler.instance().getSidedDelegate().displayMissingMods(this);
    }
    
    public List<ArtifactVersion> getModList() {
        final ImmutableList.Builder<ArtifactVersion> builder = (ImmutableList.Builder<ArtifactVersion>)ImmutableList.builder();
        for (final ModData md : this.missing) {
            builder.add((Object)new DefaultArtifactVersion(md.modId, VersionRange.createFromVersion(md.modVersion, null)));
        }
        for (final ModData md : this.badVersion) {
            builder.add((Object)new DefaultArtifactVersion(md.modId, VersionRange.createFromVersion(md.modVersion, null)));
        }
        return (List<ArtifactVersion>)builder.build();
    }
    
    private static class ModData
    {
        String modId;
        String modVersion;
    }
}
