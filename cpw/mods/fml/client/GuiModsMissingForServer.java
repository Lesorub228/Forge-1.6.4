// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import java.util.Iterator;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.network.ModMissingPacket;

public class GuiModsMissingForServer extends awe
{
    private ModMissingPacket modsMissing;
    
    public GuiModsMissingForServer(final ModMissingPacket modsMissing) {
        this.modsMissing = modsMissing;
    }
    
    public void A_() {
        this.i.add(new awl(1, this.g / 2 - 75, this.h - 38, bkb.a("gui.done")));
    }
    
    protected void a(final aut par1GuiButton) {
        if (par1GuiButton.h && par1GuiButton.g == 1) {
            FMLClientHandler.instance().getClient().a((awe)null);
        }
    }
    
    public void a(final int par1, final int par2, final float par3) {
        this.e();
        int offset = Math.max(85 - this.modsMissing.getModList().size() * 10, 10);
        this.a(this.o, "Forge Mod Loader could not connect to this server", this.g / 2, offset, 16777215);
        offset += 10;
        this.a(this.o, "The mods and versions listed below could not be found", this.g / 2, offset, 16777215);
        offset += 10;
        this.a(this.o, "They are required to play on this server", this.g / 2, offset, 16777215);
        offset += 5;
        for (final ArtifactVersion v : this.modsMissing.getModList()) {
            offset += 10;
            this.a(this.o, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.g / 2, offset, 15658734);
        }
        super.a(par1, par2, par3);
    }
}
