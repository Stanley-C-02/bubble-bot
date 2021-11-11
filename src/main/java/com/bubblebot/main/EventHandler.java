package com.bubblebot.main;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.util.Locale;

public class EventHandler {
  static Mono<?> handleReady(ReadyEvent event) {
    return Mono.fromRunnable(() -> {
      User self = event.getSelf();
      System.out.printf("Successfully logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
    });
  }

  static Mono<?> handleMessageCreate(MessageCreateEvent event) {
    Message message = event.getMessage();
    Mono<MessageChannel> channelMono = message.getChannel();
    String s = message.getContent().toLowerCase(Locale.ROOT);

    switch(s) {
      case "b!help":
        return channelMono.flatMap(channel -> channel.createMessage(String.format("My name is Bubble Bot!%nProper commands include:%n```- b!help%n- b!ping```")));
      case "b!ping":
        return channelMono.flatMap(channel -> channel.createMessage("pong!"));
      case "b!exit x":
        System.exit(0);
      default:
        if(s.matches("^b!.*")) {
          return channelMono.flatMap(channel -> channel.createMessage("Sorry, use `b!help` to list all commands."));
        } else {
          return Mono.empty();
        }
    }
  }
}
