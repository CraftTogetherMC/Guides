package de.gcmclan.team.guides;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class GuidesPlugin extends JavaPlugin implements GuideManager {
    private static final short[] VERSION = new short[] {
            1,
            14
    };

    private HashMap < CommandSender, Stack < Guide >> guides;

    public void onEnable() {
        MsgSender.cInfo(this, "Starting...");
        this.guides = new HashMap < > ();
    }

    public void onDisable() {
        MsgSender.cInfo(this, "Stopping...");
        MsgSender.cInfo(this, "Stopped!");
    }

    public void addGuide(Guide guide, boolean deactivate) {
        if (!this.guides.containsKey(guide.getCmdSender())) {

            this.guides.put(guide.getCmdSender(), new Stack < > ());
        } else if (!this.guides.get(guide.getCmdSender()).isEmpty()) {

            this.guides.get(guide.getCmdSender()).peek().setActive(deactivate);
        }
        this.guides.get(guide.getCmdSender()).push(guide);
        getServer().getPluginManager().registerEvents(guide, guide.getExecutor());
        guide.start();
    }

    public void setGuideActive(CommandSender s, boolean activate) {
        if (this.guides.containsKey(s) &&
                !this.guides.get(s).isEmpty()) {
            this.guides.get(s).peek().setActive(activate);
        }
    }

    public boolean removeGuide(CommandSender s, boolean activate) {
        if (this.guides.containsKey(s) &&
                !this.guides.get(s).isEmpty()) {

            HandlerList.unregisterAll(this.guides.get(s).pop());
            //HandlerList.unregisterAll(((Stack<Listener>)this.guides.get(s)).pop());
            if (!this.guides.get(s).isEmpty()) {

                this.guides.get(s).peek().setActive(activate);
            } else {

                this.guides.remove(s);
            }
            return true;
        }
        return false;
    }

    public boolean removeAllGuides(CommandSender s) {
        if (this.guides.containsKey(s) &&
                !this.guides.get(s).isEmpty()) {

            while (!this.guides.isEmpty()) {
                HandlerList.unregisterAll(this.guides.get(s).pop());
                //HandlerList.unregisterAll(((Stack<Listener>)this.guides.get(s)).pop());
            }
            this.guides.remove(s);
        }
        return false;
    }

    public void replaceCmdSenderInGuides(CommandSender old, CommandSender follower) {
        if (this.guides.containsKey(old) &&
                !this.guides.get(old).isEmpty()) {

            Stack < Guide > guides = this.guides.get(old);
            this.guides.remove(old);
            Iterator < Guide > it = guides.iterator();
            while (it.hasNext()) {
                it.next().setCommandSender(follower);
            }
            this.guides.put(follower, guides);
        }
    }

    public HashMap < CommandSender, Stack < Guide >> getGuides() {
        return this.guides;
    }

    public static String getVersion() {
        StringBuilder version = new StringBuilder();
        version.append(VERSION[0]);
        version.append(".");
        version.append(VERSION[1]);
        version.append("b");
        version.append(VERSION[2]);
        return version.toString();
    }
}