package com.bubblebot.main;

import com.bubblebot.main.commands.GreetCommand;
import com.bubblebot.main.commands.LeagueCommand;
import com.bubblebot.main.commands.PingCommand;
import com.bubblebot.main.commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SlashCommandListener {
  private final static List<SlashCommand> commands = new ArrayList<>();

  static {
    // Register all commands this class is initialized
    SlashCommandListener.commands.addAll(Set.of(
      new PingCommand(),
      new GreetCommand(),
      new LeagueCommand()
    ));
  }

  static Mono<Void> handle(ChatInputInteractionEvent event) {
    return Flux.fromIterable(commands)
      .filter(command -> command.getName().equalsIgnoreCase(event.getCommandName()))
      .next()
      .flatMap(command -> command.handle(event));
  }
}
