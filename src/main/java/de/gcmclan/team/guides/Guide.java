package de.gcmclan.team.guides;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;

public class Guide implements Listener {
  private final GuideManager gm;
  private final GuideReceiver gr;
  private final Plugin executor;
  private String guideSyn = "Guide";

  private CommandSender s;

  private boolean active = false;

  private final Segment[] segs;

  private Segment current;

  public Guide(GuideManager gm, GuideReceiver gr, Plugin executor, CommandSender s, Segment[] segs) {
    this.gm = gm;
    this.gr = gr;
    this.executor = executor;
    this.s = s;
    this.segs = segs;
    if (this.segs.length <= 0 ||
            this.segs[0] == null) {
      throw new IllegalArgumentException("There must be at least one Segment in array at position 0.");
    }
    this.current = this.segs[0];
  }

  public Guide(GuideManager gm, GuideReceiver gr, Plugin executor, String guideSyn, CommandSender s, Segment[] segs) {
    this(gm, gr, executor, s, segs);
    this.guideSyn = guideSyn;
  }

  @EventHandler
  void consoleChat(ServerCommandEvent ev) {
    if (this.active &&
            ev.getSender().equals(this.s)) {
      in (ev.getCommand());
    }
  }

  @EventHandler
  void playerChat(AsyncPlayerChatEvent ev) {
    if (this.active &&
            this.s instanceof org.bukkit.entity.Player &&
            ev.getPlayer().equals(this.s)) {

      ev.setCancelled(true); in (ev.getMessage());
    }
  }

  private void in (String msg) {
    if (this.current == null) {

      this.gm.removeGuide(this.s, true);
      return;
    }
    if (!this.current.isSpaceable()) {
      msg = msg.replace(" ", "");
    }
    if (msg.isEmpty()) {

      MsgSender.sBug(this.guideSyn, this.s, "You have to type in something...");
      out();
      return;
    }
    if (msg.equalsIgnoreCase("back")) {

      if (this.current.getBackSeg() >= 0 &&
              goTo(this.current.getBackSeg())) {

        MsgSender.sWarn(this.guideSyn, this.s, "You switched back, to the last option!");
        out();

        return;
      }

      exitByPlugin();

      return;
    }
    if (msg.equalsIgnoreCase("exit") ||
            msg.equalsIgnoreCase("stop")) {

      exitByPlayer();
      return;
    }
    if (msg.equalsIgnoreCase("skip") ||
            msg.equalsIgnoreCase("next")) {

      if (this.current.getSkipSeg() >= 0) {

        if (this.current.isExitBySkip()) {

          this.gr.finish(this.s);
          finish();
          this.gm.removeGuide(this.s, true);
          return;
        }
        if (!goTo(this.current.getSkipSeg())) {
          return;
        }

        MsgSender.sWarn(this.guideSyn, this.s, "You skipped the option.");
      } else {

        MsgSender.sBug(this.guideSyn, this.s, "You cannot skip this option.");
      }
      out();
      return;
    }
    int res = this.gr.receive(this, this.current, msg);
    switch (res) {
      case -3:
        exitByPlugin();
        return;
      case -2:
        finish();
        return;
      case -1:
        this.gm.removeGuide(this.s, true);
        return;
    }
    if (!goTo(res)) {
      return;
    }

    out();
  }

  private void out() {
    if (this.current == null) {

      exitByPlugin();

      return;
    }

    MsgSender.sEmpty(this.guideSyn, this.s, ChatColor.GREEN);

    String[] lines = this.current.getText().split(";");
    for (byte i = 0; i < lines.length; i = (byte)(i + 1)) {
      MsgSender.sInfo(this.guideSyn, this.s, lines[i]);
    }
    if (this.current.getSkipSeg() >= 0) {
      MsgSender.sInfo(this.guideSyn, this.s, ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You can skip this option by typing " + ChatColor.DARK_GREEN + "next" + ChatColor.GRAY + ".");
    }
  }

  public void start() {
    this.active = true;
    out();
  }

  public void setActive(boolean active) {
    if (!this.active && active) {

      MsgSender.sEmpty(this.guideSyn, this.s, ChatColor.GRAY);
      MsgSender.sInfo(this.guideSyn, this.s, "Welcome back to your Setup.");
      MsgSender.sInfo(this.guideSyn, this.s, "You stopped here:");
      out();
    } else if (this.active && !active) {

      MsgSender.sEmpty(this.guideSyn, this.s, ChatColor.GREEN);
      MsgSender.sWarn(this.guideSyn, this.s, "The Setup was paused.");
      MsgSender.sInfo(this.guideSyn, this.s, "It will resume, when you finish the other task.");
    }
    this.active = active;
  }

  public boolean isActive() {
    return this.active;
  }

  public void exitAndFollow(Guide guide) {
    this.gm.removeGuide(this.s, false);
    this.gm.addGuide(guide, true);
  }

  public final GuideManager getGuideManager() {
    return this.gm;
  }

  public final GuideReceiver getReceiver() {
    return this.gr;
  }

  public final Plugin getExecutor() {
    return this.executor;
  }

  public String getGuideSyn() {
    return this.guideSyn;
  }

  public CommandSender getCmdSender() {
    return this.s;
  }

  public boolean isGuideActive() {
    return this.active;
  }

  public Segment[] getSegs() {
    return this.segs;
  }

  public Segment getCurrent() {
    return this.current;
  }

  public void setGuideSyn(String guideSyn) {
    this.guideSyn = guideSyn;
  }

  public void setCommandSender(CommandSender s) {
    this.s = s;
  }

  public boolean goTo(int segId) {
    if (segId < -1) {
      return false;
    }
    if (segId < 0 ||
            segId >= this.segs.length ||
            this.segs[segId] == null) {

      MsgSender.cBug(this.executor, "Segment (ID = " + segId + ") not found!");
      exitByPlugin();
      return false;
    }
    this.current = this.segs[segId];
    return true;
  }

  public void exitByPlayer() {
    MsgSender.sEmpty(this.guideSyn, this.s, ChatColor.GREEN);
    MsgSender.sWarn(this.guideSyn, this.s, "You left the " + this.guideSyn + "!");
    this.gm.removeGuide(this.s, true);
  }

  public void exitByPlugin() {
    MsgSender.sEmpty(this.guideSyn, this.s, ChatColor.GREEN);
    MsgSender.sWarn(this.guideSyn, this.s, "The " + this.guideSyn + " was exited!");
    this.gm.removeGuide(this.s, true);
  }

  public void finish() {
    MsgSender.sEmpty(this.guideSyn, this.s, ChatColor.GREEN);
    MsgSender.sInfo(this.guideSyn, this.s, "You finished the " + this.guideSyn + "!");
    this.gm.removeGuide(this.s, true);
  }
}