package com.bubblebot.main.commands;

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
    System.out.println("EVENT:");
    System.out.println(event.getCommandName());
    System.out.println(event.getCommandType().name());

    final String option1 = tempOption.getName();
    System.out.println("OPTION:");
    System.out.println(tempOption.getName());
    System.out.println(tempOption.getType().name());

    tempOption = tempOption.getOptions().get(0);
    System.out.println("OPTION:");
    System.out.println(tempOption.getName());
    System.out.println(tempOption.getType().name());

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
    return this.getSummoner(event, args[0], region);
  }

  private Mono<Void> getSummoner(final ChatInputInteractionEvent event, final String name, final String region) {
    return event.reply()
      .withEphemeral(true)
      .withContent("getSummoner TO BE IMPLEMENTED");
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