package com.bubblebot.main;

/**Bubble Bot
 * A discord bot made using Discord4J
 */

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Platform;
import com.merakianalytics.orianna.types.common.Region;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;

import java.util.Scanner;

/**
 * Initializes the bot, commands, and listeners to handle functionality
 *
 * Discord4J and Orianna use given args, where
 * - arg[0] is the Discord bot token
 * - arg[1] is the Discord server ID
 * - arg[2] is the Riot API key
 */
public class Bot {
  public static void main(String[] args) {
    final GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
      .build()
      .login()
      .block();

    final long guildId = Long.parseLong(args[1]); // My Testing server
//    final long guildId = Long.parseLong(System.getenv("SERVER_ID")); // My Testing server
    Orianna.setRiotAPIKey(args[2]);
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

    Scanner reader = new Scanner(System.in);
    while(!reader.next().equalsIgnoreCase("exit"))
      System.out.println("Enter exit to stop bot");
    System.out.println("EXITING");
//    client.onDisconnect().block();
  }
}
