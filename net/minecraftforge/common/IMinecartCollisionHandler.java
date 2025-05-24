// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

public interface IMinecartCollisionHandler
{
    void onEntityCollision(final st p0, final nn p1);
    
    asx getCollisionBox(final st p0, final nn p1);
    
    asx getMinecartCollisionBox(final st p0);
    
    asx getBoundingBox(final st p0);
}
