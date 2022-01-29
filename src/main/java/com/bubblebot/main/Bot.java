package com.bubblebot.main;

/**Bubble Bot
 * A discord bot made without Discord4J or Orianna
 * To learn more about APIs/REST/HTTP(S) requests
 *
 * Initializes the bot, commands, and listeners to handle functionality
 *
 * Discord4J and Orianna use given args, where
 * - arg[0] is the Discord bot token
 * - arg[1] is the Discord server ID
 * - arg[2] is the Riot API key
 */
public class Bot {
  public static String
    botToken = null,
    guildId = null,
    riotAPIKey = null;

  public static void main(final String[] args) {
    assert(args.length == 3);
    botToken = args[0];
    guildId = args[1];
    riotAPIKey = args[2];

    System.out.println("TO BE IMPLEMENTED");
  }
}
