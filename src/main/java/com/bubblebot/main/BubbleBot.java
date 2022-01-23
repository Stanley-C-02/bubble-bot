package com.bubblebot.main;

/**Bubble BubbleBot
 * A discord bot made using Discord4J
 */

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Platform;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.common.Side;
import com.merakianalytics.orianna.types.core.championmastery.ChampionMasteries;
import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.Team;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.merakianalytics.orianna.types.core.championmastery.ChampionMastery;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BubbleBot {
  static String cmdPrefix = "b!";

  private final Map<String, Command> commands;

  private BubbleBot() {
    this.commands = new HashMap<>();

    this.commands.putAll(new Commands().toMap());

//    this.commands.put("embed", event -> event.getMessage().getChannel()
//      .flatMap(channel -> channel
//        .createMessage(
//          EmbedCreateSpec.builder()
//            .addField("Inline Field", String.format("Two%nlines"), true)
//            .addField("Inline Too", "yay", true)
//            .addField("I am also inline", "I am happy", true)
//            .addField("But not me", "sadly", false)
//            .addField("\u200B", "\u200B", false)
//            .color(Color.CYAN)
//            .title("Embed Title with URL!")
//            .url("https://discord4j.com")
//            .author("Author Here (Google)", "https://www.google.com/", "https://icon-library.com/images/ios-settings-icon/ios-settings-icon-24.jpg") // top left
//            .description("Lorem ipsum just kidding... here is the description")
//            .thumbnail("https://vectorified.com/images/instagram-icon-black-white-26.jpg") // top right
//            .image("https://i.imgur.com/F9BhEoz.png") // large
//            .timestamp(Instant.EPOCH)
//            .footer("Footer Text", "https://image.flaticon.com/icons/png/512/32/32206.png") // bottom left
//            .build())
//        .then()));
//
//    this.commands.put("time", event -> event.getMessage().getChannel()
//      .flatMap(channel -> channel.createMessage("It is currently " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"))))
//      .then());
  }

  public static void main(String[] args) {
    Map<String[], Integer> temp = new HashMap<>();
    temp.put(new String[] {"first", "second", "third"}, 1);
    temp.put(new String[] {"1st", "second"}, 2);
    temp.put(new String[] {"a", "b", "c", "d", "e"}, 3);

    final boolean found = temp.keySet().stream()
      .anyMatch(strings -> Arrays.stream(strings)
        .anyMatch(string -> {System.out.printf("current: %s vs second %b%n", string, string.equals("seconxd"));return string.equals("seconxd");}));

    System.out.println(found);

//    final boolean found = this.mappings.keySet().stream()
//      .noneMatch(strings -> Arrays.stream(strings)
//        .noneMatch(string -> string.equalsIgnoreCase(event.getMessage().getContent())));

    Orianna.setRiotAPIKey(Token.RIOT_API_KEY);
    Orianna.setDefaultPlatform(Platform.NORTH_AMERICA);
    Orianna.setDefaultRegion(Region.NORTH_AMERICA);
//    BubbleBot.bestChampions("FatalElement");
//    BubbleBot.lastMatchKDA("FatalElement");

    final GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
      .build()
      .login()
      .block();

    final BubbleBot bubbleBot = new BubbleBot();

    client.getEventDispatcher().on(ReadyEvent.class)
      .subscribe(event -> {
        final User self = event.getSelf();
        System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
      });

    client.getEventDispatcher().on(MessageCreateEvent.class)
      // 3.1 Message.getContent() is a String
      .flatMap(event -> Mono.just(event.getMessage().getContent())
        .flatMap(content -> Flux.fromIterable(bubbleBot.commands.entrySet())
          // We will be using ! as our "prefix" to any command in the system.
          .filter(entry -> content.toLowerCase(Locale.ROOT).startsWith(BubbleBot.cmdPrefix + entry.getKey()))
          .flatMap(entry -> entry.getValue().execute(event))
          .next()))
      .subscribe();

    client.onDisconnect().block();
  }

  public static void bestChampions(final String summonerName) {
    final Summoner summoner = Summoner.named(summonerName).get();
    final ChampionMasteries masteries = summoner.getChampionMasteries();
    final List<ChampionMastery> goodWith = masteries.filter((ChampionMastery mastery) -> mastery.getLevel() >= 6);

    final List<String> names = goodWith.stream().map((ChampionMastery mastery) -> mastery.getChampion().getName()).collect(Collectors.toList());
    System.out.printf("%s is good at: [ %s ]%n", summonerName, String.join(", ", names));
  }

  public static void lastMatchKDA(final String summonerName) {
    Summoner summoner = Summoner.named("FatalElement").get();
    MatchHistory matches = summoner.matchHistory().get();
//    Match lastMatch = matches.get(0); // TODO: NOT WORKING FIX?

//    final Participant summonerParticipant = lastMatch.getParticipants().find((Participant participant) -> participant.getSummoner().equals(summoner));
//
//    int kills = 0, deaths = 0, assists = 0;
//
//    for(Participant participant : summonerParticipant.getTeam().getParticipants()) {
//      kills += participant.getStats().getKills();
//      deaths += participant.getStats().getDeaths();
//      assists += participant.getStats().getAssists();
//    }
//
//    System.out.printf("%s's team had a KDA of %d/%d/%d%n", summonerName, kills, deaths, assists);
//
//    kills = 0; deaths = 0; assists = 0;
//    Team otherTeam = summonerParticipant.getTeam().getSide() == Side.BLUE ? lastMatch.getRedTeam() : lastMatch.getBlueTeam();
//
//    for(Participant participant : otherTeam.getParticipants()) {
//      kills += participant.getStats().getKills();
//      deaths += participant.getStats().getDeaths();
//      assists += participant.getStats().getAssists();
//    }
//
//    System.out.printf("The other team had a KDA of %d/%d/%d%n", kills, deaths, assists);
  }
}
