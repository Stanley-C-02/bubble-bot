package com.bubblebot.main.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class LeagueCommand implements SlashCommand {
  @Override
  public String getName() {
    System.out.println(1);
    return "lol";
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    System.out.println("Found lol command");
    System.out.println(event.getOptions().get(0));

    return event.reply()
      .withEphemeral(true)
      .withContent(event.getOptions().stream()
      .map(option -> option.getValue().toString())
      .reduce(this.getName(),
        (acc, option) -> {
        return String.format("%s %s", acc, option);
      }));
  }
}
