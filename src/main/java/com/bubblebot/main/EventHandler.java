package com.bubblebot.main;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class EventHandler {
  private Map<String[], Consumer<?>> commands;

  EventHandler() {
    this.commands = new HashMap<>();

    this.commands.put(new String[] { "b!help" }, (event) -> {return event});
    this.commands.put(new String[] { "b!ping" }, (event) -> {return event});
    this.commands.put(new String[] { "b!exit x" }, (event) -> {return event});
  }

  Mono<?> handleReady(ReadyEvent event) {
    return Mono.fromRunnable(() -> {
      User self = event.getSelf();
      System.out.printf("Successfully logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
    });
  }

  Mono<?> handleMessageCreate(MessageCreateEvent event) {
    Message message = event.getMessage();
    Mono<MessageChannel> channelMono = message.getChannel();
    String usercmd = message.getContent().toLowerCase(Locale.ROOT);

    switch(usercmd) {
      case "b!help":
        return channelMono.flatMap(channel -> channel.createMessage(String.format("My name is Bubble Bot!%nProper commands include:%n```- b!help%n- b!ping```")));
      case "b!ping":
        return channelMono.flatMap(channel -> channel.createMessage("pong!"));
      case "b!exit x":
        System.exit(0);
      default:
        if(usercmd.matches("^b!.*")) {
          return channelMono.flatMap(channel -> channel.createMessage("Sorry, use `b!help` to list all commands."));
        } else {
          return Mono.empty();
        }
    }
  }

  Mono<?> parseUserCommand(String s) {
    if(this.commands.indexOf(s) == -1) {
      // invalid command entered
    }
    return null;
  }
}
