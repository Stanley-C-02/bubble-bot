package com.bubblebot.main.commands;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.core.league.League;
import com.merakianalytics.orianna.types.core.staticdata.Champion;
import com.merakianalytics.orianna.types.core.staticdata.Champions;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LeagueCommand implements SlashCommand {
  private Map<String, Map<String, BiFunction<ChatInputInteractionEvent, String[], Mono<Void>>>> mapping = Map.of(
    "summoner", Map.of("get", this::getSummoner),
    "champion", Map.of("get", this::getChampion)
  );

  @Override
  public String getName() {
    System.out.println(1);
    return "lol";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    ApplicationCommandInteractionOption tempOption = event.getOptions().get(0);
//    System.out.println("EVENT:");
//    System.out.println(event.getCommandName());
//    System.out.println(event.getCommandType().name());

    final String option1 = tempOption.getName();
//    System.out.println("OPTION:");
//    System.out.println(tempOption.getName());
//    System.out.println(tempOption.getType().name());

    tempOption = tempOption.getOptions().get(0);
//    System.out.println("OPTION:");
//    System.out.println(tempOption.getName());
//    System.out.println(tempOption.getType().name());

    final String option2 = tempOption.getName();

    final String[] options = tempOption.getOptions().stream()
      .map(ApplicationCommandInteractionOption::getValue)
      .map(Optional::get)
      .map(ApplicationCommandInteractionOptionValue::asString)
      .toArray(String[]::new);

    return this.mapping.get(option1).get(option2).apply(event, options);
  }

  private Mono<Void> getSummoner(final ChatInputInteractionEvent event, final String[] args) {
    final String region = args.length < 2 ? "NA" : args[1];
    return event.reply()
      .withEphemeral(true)
      .withContent(this.getSummoner(event, args[0], region));
  }

  private String getSummoner(final ChatInputInteractionEvent event, final String name, final String region) {
    final Summoner summoner = Orianna.summonerNamed(name).get();

    if(!summoner.exists()) return String.format("%s does not exist on the %s server", summoner.getName(), summoner.getRegion());

    final Champions champions = Orianna.getChampions();
    final Champion randomChampion = champions.get((int)Math.random() * champions.size());
    final League challengerLeague = Orianna.challengerLeagueInQueue(Queue.RANKED_SOLO).get();
    final Summoner bestNA = challengerLeague.get(0).getSummoner();

    String info = String.format("%s is level %d on the %s server%n", summoner.getName(), summoner.getLevel(), summoner.getRegion());
    info += String.format("They play champions such as %s%n", randomChampion.getName());
    info += String.format("But are not as good as %s at League", bestNA.getName());

    return info;
  }

  private Mono<Void> getChampion(final ChatInputInteractionEvent event, final String[] args) {
    final String category = args.length < 2 ? "NA" : args[1];
    return this.getChampion(event, args[0], category);
  }

  private Mono<Void> getChampion(final ChatInputInteractionEvent event, final String name, final String category) {
    return event.reply()
      .withEphemeral(true)
      .withContent("getChampion TO BE IMPLEMENTED");
  }
}

/*

lol <group> <action> [...args]

lol => summoner => get [args]
    => champion => get [args]
 */