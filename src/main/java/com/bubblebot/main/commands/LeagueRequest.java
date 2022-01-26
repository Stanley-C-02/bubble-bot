package com.bubblebot.main.commands;

import discord4j.discordjson.json.ApplicationCommandRequest;

public class LeagueRequest {
  public ApplicationCommandRequest build() {
    return ApplicationCommandRequest.builder()
      .name("aname")
      .description("adescription")
      .build();
  }
}
