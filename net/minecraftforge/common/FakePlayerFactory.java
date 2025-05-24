// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.HashMap;
import java.util.Map;

public class FakePlayerFactory
{
    private static Map<String, FakePlayer> fakePlayers;
    private static FakePlayer MINECRAFT_PLAYER;
    
    public static FakePlayer getMinecraft(final abw world) {
        if (FakePlayerFactory.MINECRAFT_PLAYER == null) {
            FakePlayerFactory.MINECRAFT_PLAYER = get(world, "[Minecraft]");
        }
        return FakePlayerFactory.MINECRAFT_PLAYER;
    }
    
    public static FakePlayer get(final abw world, final String username) {
        if (!FakePlayerFactory.fakePlayers.containsKey(username)) {
            final FakePlayer fakePlayer = new FakePlayer(world, username);
            FakePlayerFactory.fakePlayers.put(username, fakePlayer);
        }
        return FakePlayerFactory.fakePlayers.get(username);
    }
    
    static {
        FakePlayerFactory.fakePlayers = new HashMap<String, FakePlayer>();
        FakePlayerFactory.MINECRAFT_PLAYER = null;
    }
}
