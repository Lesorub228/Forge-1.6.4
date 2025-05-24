// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import java.util.Iterator;
import java.io.File;
import cpw.mods.fml.common.ModContainer;
import java.util.Map;
import cpw.mods.fml.common.DuplicateModsFoundException;

public class GuiDupesFound extends avh
{
    private DuplicateModsFoundException dupes;
    
    public GuiDupesFound(final DuplicateModsFoundException dupes) {
        super((String)null, (String)null);
        this.dupes = dupes;
    }
    
    public void A_() {
        super.A_();
    }
    
    public void a(final int par1, final int par2, final float par3) {
        this.e();
        int offset = Math.max(85 - this.dupes.dupes.size() * 10, 10);
        this.a(this.o, "Forge Mod Loader has found a problem with your minecraft installation", this.g / 2, offset, 16777215);
        offset += 10;
        this.a(this.o, "You have mod sources that are duplicate within your system", this.g / 2, offset, 16777215);
        offset += 10;
        this.a(this.o, "Mod Id : File name", this.g / 2, offset, 16777215);
        offset += 5;
        for (final Map.Entry<ModContainer, File> mc : this.dupes.dupes.entries()) {
            offset += 10;
            this.a(this.o, String.format("%s : %s", mc.getKey().getModId(), mc.getValue().getName()), this.g / 2, offset, 15658734);
        }
    }
}
