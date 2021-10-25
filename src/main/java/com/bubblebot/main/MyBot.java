package com.bubblebot.main;

/**Bubble Bot
 * A discord bot made using Discord4J
 */

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.Locale;

public class MyBot {
  public static void main(String[] args) {
    // DiscordClient only provides operations while not logged in
    DiscordClient client = DiscordClient.create(Token.TOKEN);

    // GatewayDiscordClient provides connection
    // Invokes given function

    // returning Mono.empty() does "nothing"
    // Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> Mono.empty());

    Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
      // ReadyEvent example
      // handled by printing the username and discriminator (tag) of the 'self user',
      // which is the user associated w/ the bot account
      Mono<Void> handleReadyEvent = gateway.on(ReadyEvent.class, event ->
        Mono.fromRunnable(() -> {
          final User self = event.getSelf();
          System.out.printf("Successfully logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
        })
      ).then();

      // MessageCreateEvent example
      Mono<Void> handleMessageCreate = gateway.on(MessageCreateEvent.class, event -> {
        Message message = event.getMessage();

        switch(message.getContent().toLowerCase(Locale.ROOT)) {
          case "!help":
            return message.getChannel().flatMap(channel -> channel.createMessage("My name is Bubble Bot!"));
          case "!ping":
            return message.getChannel().flatMap(channel -> channel.createMessage("pong!"));
          default:
            return Mono.empty();
        }
      }).then();

      // Combine commands
      return handleReadyEvent.and(handleMessageCreate);
    });

    login.block();
  }
}
