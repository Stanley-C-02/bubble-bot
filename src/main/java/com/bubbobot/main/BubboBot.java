package com.bubbobot.main;

/**Bubble BubboBot
 * A discord bot made using Discord4J
 */

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import discord4j.voice.AudioProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class BubboBot {
  static String cmdPrefix = "b!";

  private final Map<String, Command> commands;

  private BubboBot() {
    this.commands = new HashMap<>();

    Command help = event -> event.getMessage().getChannel()
      .flatMap(channel -> channel.createMessage(
        String.format("can perform these commands:%n%s",
          this.commands.keySet().stream()
            .sorted()
            .map(cmd -> String.format("%s%s", BubboBot.cmdPrefix, cmd))
            .collect(Collectors.joining(", ")))))
      .then();

    this.commands.put("?", help);

    this.commands.put("help", help);

    this.commands.put("embed", event -> event.getMessage().getChannel()
      .flatMap(channel -> channel
        .createMessage(
          EmbedCreateSpec.builder()
            .addField("Inline Field", String.format("Two%nlines"), true)
            .addField("Inline Too", "yay", true)
            .addField("I am also inline", "I am happy", true)
            .addField("But not me", "sadly", false)
            .addField("\u200B", "\u200B", false)
            .color(Color.CYAN)
            .title("Embed Title with URL!")
            .url("https://discord4j.com")
            .author("Author Here (Google)", "https://www.google.com/", "https://icon-library.com/images/ios-settings-icon/ios-settings-icon-24.jpg") // top left
            .description("Lorem ipsum just kidding... here is the description")
            .thumbnail("https://vectorified.com/images/instagram-icon-black-white-26.jpg") // top right
            .image("https://i.imgur.com/F9BhEoz.png") // large
            .timestamp(Instant.EPOCH)
            .footer("Footer Text", "https://image.flaticon.com/icons/png/512/32/32206.png") // bottom left
            .build())
        .then()));

    this.commands.put("time", event -> event.getMessage().getChannel()
      .flatMap(channel -> channel.createMessage("It is currently " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"))))
      .then());
  }

  public static void main(String[] args) {
    final GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
      .build()
      .login()
      .block();

    final BubboBot bubboBot = new BubboBot();

    client.getEventDispatcher().on(ReadyEvent.class)
      .subscribe(event -> {
        final User self = event.getSelf();
        System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
      });

    client.getEventDispatcher().on(MessageCreateEvent.class)
      // 3.1 Message.getContent() is a String
      .flatMap(event -> Mono.just(event.getMessage().getContent())
        .flatMap(content -> Flux.fromIterable(bubboBot.commands.entrySet())
          // We will be using ! as our "prefix" to any command in the system.
          .filter(entry -> content.toLowerCase(Locale.ROOT).startsWith(BubboBot.cmdPrefix + entry.getKey()))
          .flatMap(entry -> entry.getValue().execute(event))
          .next()))
      .subscribe();

    client.onDisconnect().block();
  }
}
