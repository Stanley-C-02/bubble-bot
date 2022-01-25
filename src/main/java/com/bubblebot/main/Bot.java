package com.bubblebot.main;

/**Bubble Bot
 * A discord bot made using Discord4J
 */

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Bot {
  public static String cmdPrefix = "b!";

  public static void main(String[] args) {
    final GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
      .build()
      .login()
      .block();

    long guildId = 898745468878745631L; // My Testing server

    try {
      new GuildCommandRegistrar(guildId, client.getRestClient()).registerCommands();
    } catch(Exception e) {
      e.printStackTrace();
    }

    client.getEventDispatcher().on(ReadyEvent.class)
      .subscribe(event -> {
        final User self = event.getSelf();
        System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
      });

    client.getEventDispatcher().on(ChatInputInteractionEvent.class)
      .flatMap(SlashCommandListener::handle)
      .subscribe();

    client.onDisconnect().block();
  }
}
