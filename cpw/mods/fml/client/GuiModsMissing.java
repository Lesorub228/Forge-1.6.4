// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import java.util.Iterator;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.MissingModsException;

public class GuiModsMissing extends avh
{
    private MissingModsException modsMissing;
    
    public GuiModsMissing(final MissingModsException modsMissing) {
        super((String)null, (String)null);
        this.modsMissing = modsMissing;
    }
    
    public void A_() {
        super.A_();
    }
    
    public void a(final int par1, final int par2, final float par3) {
        this.e();
        int offset = Math.max(85 - this.modsMissing.missingMods.size() * 10, 10);
        this.a(this.o, "Forge Mod Loader has found a problem with your minecraft installation", this.g / 2, offset, 16777215);
        offset += 10;
        this.a(this.o, "The mods and versions listed below could not be found", this.g / 2, offset, 16777215);
        offset += 5;
        for (final ArtifactVersion v : this.modsMissing.missingMods) {
            offset += 10;
            if (v instanceof DefaultArtifactVersion) {
                final DefaultArtifactVersion dav = (DefaultArtifactVersion)v;
                if (dav.getRange() != null && dav.getRange().isUnboundedAbove()) {
                    this.a(this.o, String.format("%s : minimum version required is %s", v.getLabel(), dav.getRange().getLowerBoundString()), this.g / 2, offset, 15658734);
                    continue;
                }
            }
            this.a(this.o, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.g / 2, offset, 15658734);
        }
        offset += 20;
        this.a(this.o, "The file 'ForgeModLoader-client-0.log' contains more information", this.g / 2, offset, 16777215);
    }
}
