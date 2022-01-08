package com.bubbobot.main;

/**Bubble BubboBot
 * A discord bot made using Discord4J
 */

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        String.format("Valid commands include:%n%s",
          this.commands.keySet().stream()
            .sorted()
            .map(cmd -> String.format("%s%s", BubboBot.cmdPrefix, cmd))
            .collect(Collectors.joining(", ")))))
      .then();

    commands.put("?", help);

    commands.put("help", help);

    commands.put("ping", event -> event.getMessage().getChannel()
      .flatMap(channel -> channel.createMessage("Pong!"))
      .then());

    commands.put("time", event -> event.getMessage().getChannel()
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
//    // DiscordClient only provides operations while not logged in
//    DiscordClient client = DiscordClient.create(Token.DISCORD_TOKEN);
//
//    // GatewayDiscordClient provides connection
//    // Invokes given function
//
//    // returning Mono.empty() does "nothing"
//    // Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> Mono.empty());
//
//    Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
//      EventHandler handler = new EventHandler();
//      // ReadyEvent example
//      // handled by printing the username and discriminator (tag) of the 'self user',
//      // which is the user associated w/ the bubboBot account
//      Mono<Void> handleReadyEvent = gateway.on(ReadyEvent.class, handler::handleReady).then();
//
//      // MessageCreateEvent example
//      Mono<Void> handleMessageCreate = gateway.on(MessageCreateEvent.class, handler::handleMessageCreate).then();
//
//      // Combine commands
//      return handleReadyEvent.and(handleMessageCreate);
//    })
//    login.block();
  }
}
