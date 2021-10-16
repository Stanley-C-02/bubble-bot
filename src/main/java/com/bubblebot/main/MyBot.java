package com.bubblebot.main;

import discord4j.core.DiscordClient;

public class MyBot {
  public static void main(String[] args) {
    // DiscordClient only provides operations while not logged in
    DiscordClient client = DiscordClient.create("TOKEN");

    // GatewayDiscordClient provides connection
    Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> Mono.empty());

    login.block();

    // Branch testing
  }
}