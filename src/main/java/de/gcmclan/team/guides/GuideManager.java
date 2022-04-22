package de.gcmclan.team.guides;

import java.util.HashMap;
import java.util.Stack;
import org.bukkit.command.CommandSender;

public interface GuideManager {
  void addGuide(Guide paramGuide, boolean paramBoolean);

  boolean removeGuide(CommandSender paramCommandSender, boolean paramBoolean);

  boolean removeAllGuides(CommandSender paramCommandSender);

  void setGuideActive(CommandSender paramCommandSender, boolean paramBoolean);

  void replaceCmdSenderInGuides(CommandSender paramCommandSender1, CommandSender paramCommandSender2);

  HashMap < CommandSender, Stack < Guide >> getGuides();
}