// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

public class GuiCustomModLoadingErrorScreen extends avh
{
    private CustomModLoadingErrorDisplayException customException;
    
    public GuiCustomModLoadingErrorScreen(final CustomModLoadingErrorDisplayException customException) {
        super((String)null, (String)null);
        this.customException = customException;
    }
    
    public void A_() {
        super.A_();
        this.customException.initGui(this, this.o);
    }
    
    public void a(final int par1, final int par2, final float par3) {
        this.e();
        this.customException.drawScreen(this, this.o, par1, par2, par3);
    }
}
