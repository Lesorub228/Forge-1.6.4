// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client;

import java.util.List;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraftforge.event.Event;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

public class ClientCommandHandler extends aa
{
    public static final ClientCommandHandler instance;
    public String[] latestAutoComplete;
    
    public ClientCommandHandler() {
        this.latestAutoComplete = null;
    }
    
    public int a(final ad sender, String message) {
        message = message.trim();
        if (message.startsWith("/")) {
            message = message.substring(1);
        }
        final String[] temp = message.split(" ");
        final String[] args = new String[temp.length - 1];
        final String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        final ab icommand = this.a().get(commandName);
        try {
            if (icommand == null) {
                return 0;
            }
            if (icommand.a(sender)) {
                final CommandEvent event = new CommandEvent(icommand, sender, args);
                if (!MinecraftForge.EVENT_BUS.post(event)) {
                    icommand.b(sender, args);
                    return 1;
                }
                if (event.exception != null) {
                    throw event.exception;
                }
                return 0;
            }
            else {
                sender.a(this.format("commands.generic.permission").a(a.m));
            }
        }
        catch (final bd wue) {
            sender.a(this.format("commands.generic.usage", this.format(wue.getMessage(), wue.a())).a(a.m));
        }
        catch (final ay ce) {
            sender.a(this.format(ce.getMessage(), ce.a()).a(a.m));
        }
        catch (final Throwable t) {
            sender.a(this.format("commands.generic.exception").a(a.m));
            t.printStackTrace();
        }
        return 0;
    }
    
    private cv format(final String str, final Object... args) {
        return cv.b(str, args);
    }
    
    private cv format(final String str) {
        return cv.e(str);
    }
    
    public void autoComplete(String leftOfCursor, final String full) {
        this.latestAutoComplete = null;
        if (leftOfCursor.charAt(0) == '/') {
            leftOfCursor = leftOfCursor.substring(1);
            final atv mc = FMLClientHandler.instance().getClient();
            if (mc.n instanceof auw) {
                final List<String> commands = this.b((ad)mc.h, leftOfCursor);
                if (commands != null && !commands.isEmpty()) {
                    if (leftOfCursor.indexOf(32) == -1) {
                        for (int i = 0; i < commands.size(); ++i) {
                            commands.set(i, a.h + "/" + commands.get(i) + a.v);
                        }
                    }
                    else {
                        for (int i = 0; i < commands.size(); ++i) {
                            commands.set(i, a.h + commands.get(i) + a.v);
                        }
                    }
                    this.latestAutoComplete = commands.toArray(new String[commands.size()]);
                }
            }
        }
    }
    
    static {
        instance = new ClientCommandHandler();
    }
}
