// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.io.File;

public class WorldSpecificSaveHandler implements amc
{
    private js world;
    private amc parent;
    private File dataDir;
    
    public WorldSpecificSaveHandler(final js world, final amc parent) {
        this.world = world;
        this.parent = parent;
        (this.dataDir = new File(world.getChunkSaveLocation(), "data")).mkdirs();
    }
    
    public als d() {
        return this.parent.d();
    }
    
    public void c() throws aca {
        this.parent.c();
    }
    
    public adw a(final aei var1) {
        return this.parent.a(var1);
    }
    
    public void a(final als var1, final by var2) {
        this.parent.a(var1, var2);
    }
    
    public void a(final als var1) {
        this.parent.a(var1);
    }
    
    public amq e() {
        return this.parent.e();
    }
    
    public void a() {
        this.parent.a();
    }
    
    public String g() {
        return this.parent.g();
    }
    
    public File b(final String name) {
        return new File(this.dataDir, name + ".dat");
    }
}
