package com.bubblebot.main;

/**Bubble Bot
 * A discord bot made using Discord4J
 */

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Platform;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;

import java.util.Locale;

/**
 * Initializes the bot, commands, and listeners to handle functionality
 *
 * Discord4J and Orianna use set environment variables
 */
public class Bot {
  public static void main(String[] args) {
    final GatewayDiscordClient client = DiscordClientBuilder.create(System.getenv("DISCORD_TOKEN"))
      .build()
      .login()
      .block();

    final long guildId = 898745468878745631L; // My Testing server
    Orianna.setDefaultRegion(Region.NORTH_AMERICA);
    Orianna.setDefaultPlatform(Platform.NORTH_AMERICA);

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

    client.on(ChatInputInteractionEvent.class)
      .flatMap(SlashCommandListener::handle)
      .subscribe();

    client.onDisconnect().block();
  }
}
