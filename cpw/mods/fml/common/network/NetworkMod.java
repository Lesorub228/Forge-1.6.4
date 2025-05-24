// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface NetworkMod {
    boolean clientSideRequired() default false;
    
    boolean serverSideRequired() default false;
    
    String[] channels() default {};
    
    String versionBounds() default "";
    
    Class<? extends IPacketHandler> packetHandler() default NULL.class;
    
    Class<? extends ITinyPacketHandler> tinyPacketHandler() default NULL.class;
    
    Class<? extends IConnectionHandler> connectionHandler() default NULL.class;
    
    SidedPacketHandler clientPacketHandlerSpec() default @SidedPacketHandler(channels = {}, packetHandler = NULL.class);
    
    SidedPacketHandler serverPacketHandlerSpec() default @SidedPacketHandler(channels = {}, packetHandler = NULL.class);
    
    public @interface SidedPacketHandler {
        String[] channels();
        
        Class<? extends IPacketHandler> packetHandler();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface VersionCheckHandler {
    }
    
    public interface NULL extends IPacketHandler, IConnectionHandler, ITinyPacketHandler
    {
    }
}
