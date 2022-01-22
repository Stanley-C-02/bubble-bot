package com.bubblebot.main;

import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class EventHandler {
  private Map<String[], Function<Event, Mono<?>>> commands;

  EventHandler() {
    this.commands = new HashMap<>();

    this.commands.put(new String[] { "b!help", "b!?" }, (event) ->
    {
      MessageCreateEvent e = (MessageCreateEvent)event;
      Message message = e.getMessage();
      Mono<MessageChannel> channelMono = message.getChannel();
      return channelMono.flatMap(channel -> channel.createMessage(String.format("My name is Bubble BubbleBot!%nProper commands include:%n```- b!help%n- b!ping```")));
    });
    this.commands.put(new String[] { "b!ping" }, (event) -> null);
    this.commands.put(new String[] { "b!exit x" }, (event) -> null);
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

    Function<Event, Mono<?>> func = this.parseUserCommand(usercmd);

//    if(func == null) {
//      if(usercmd.matches("^b!.*$")) {
//        return channelMono.flatMap(channel -> channel.createMessage("Enter `b!help` to get all commands"));
//      } else {
//        return Mono.empty();
//      }
//    } else {
//      return func.apply(event);
//    }

    switch(usercmd) {
      case "b!help":
        return channelMono.flatMap(channel -> channel.createMessage(String.format("My name is Bubble BubbleBot!%nProper commands include:%n```- b!help%n- b!ping```")));
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

  Function<Event, Mono<?>> parseUserCommand(String s) {
    Function<Event, Mono<?>> func = this.commands.entrySet().stream()
      .filter(entry -> Arrays.asList(entry.getKey()).contains(s))
      .map(entry -> entry.getValue())
      .findFirst()
      .orElse(null);
    return func;
  }
}
