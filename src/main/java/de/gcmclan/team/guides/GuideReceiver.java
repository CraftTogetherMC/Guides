package de.gcmclan.team.guides;

import org.bukkit.command.CommandSender;

public interface GuideReceiver {
  int receive(Guide paramGuide, Segment paramSegment, String paramString);

  void finish(CommandSender paramCommandSender);

  boolean usesId(String paramString);
}