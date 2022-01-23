package com.bubblebot.main.Commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class League extends MessageCreateCommand {
  @Override
  public Mono<Void> execute(MessageCreateEvent event) {
    return null;
  }
}
