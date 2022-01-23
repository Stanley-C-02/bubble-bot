package com.bubblebot.main;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * uses the java reflection api
 */

public class Commands {
  public Map<String[], Command> mappings;

  public Commands() {
    this.mappings = new HashMap<>();

    for(final Method method : Commands.class.getDeclaredMethods()) {
      try {
        if(method.getReturnType() == Map.Entry.class) {
          final Map.Entry<String[], Command> entry = (Map.Entry<String[], Command>)method.invoke(this);
          this.mappings.put(entry.getKey(), entry.getValue());
        }
      } catch(final Exception e) {
        System.out.printf("warning: method %s in Commands.java not added to the list of runnable user commands%n", method.getName());
      }
    }
  }

  public Map<String, Command> toMap() {
    final Map<String, Command> cmds = new HashMap<>();

    for(final Map.Entry<String[], Command> entry : this.mappings.entrySet()) {
      for(final String s : entry.getKey()) {
        cmds.put(s, entry.getValue());
      }
    }

    return cmds;
  }

  private Map.Entry<String[], Command> cmdHelp() {
    return Map.entry(new String[] {"?", "help"},
      event -> event.getMessage().getChannel()
        .flatMap(channel -> channel.createMessage(
          String.format("can perform these commands:%n%s",
            this.mappings.keySet().stream()
              .flatMap(Arrays::stream)
              .sorted()
              .map(cmd -> String.format("%s%s", BubbleBot.cmdPrefix, cmd))
              .collect(Collectors.joining(", ")))))
        .then());
  }

  private Map.Entry<String[], Command> cmdEmbed() {
    return Map.entry(new String[] {"embed"},
      event -> event.getMessage().getChannel()
        .flatMap(channel -> channel
          .createMessage(
            EmbedCreateSpec.builder()
              .addField("Inline Field", String.format("Two%nlines"), true)
              .addField("Inline Too", "yay", true)
              .addField("I am also inline", "I am happy", true)
              .addField("But not me", "sadly", false)
              .addField("\u200B", "\u200B", false)
              .color(Color.CYAN)
              .title("Embed Title with URL!")
              .url("https://discord4j.com")
              .author("Author Here (Google)", "https://www.google.com/", "https://icon-library.com/images/ios-settings-icon/ios-settings-icon-24.jpg") // top left
              .description("Lorem ipsum just kidding... here is the description")
              .thumbnail("https://vectorified.com/images/instagram-icon-black-white-26.jpg") // top right
              .image("https://i.imgur.com/F9BhEoz.png") // large
              .timestamp(Instant.EPOCH)
              .footer("Footer Text", "https://image.flaticon.com/icons/png/512/32/32206.png") // bottom left
              .build())
          .then()));
  }

  private Map.Entry<String[], Command> cmdOther() {
    return Map.entry(new String[] {""},
      event -> {
      final String noPrefixCmd = event.getMessage().getContent().toLowerCase(Locale.ROOT).replaceFirst(BubbleBot.cmdPrefix, "");
      final boolean validCmd = this.mappings.keySet().stream()
        .anyMatch(strings -> Arrays.stream(strings)
          .anyMatch(string -> {System.out.printf("user: %s'%s' and proper: '%s' equal? %b%n", BubbleBot.cmdPrefix, noPrefixCmd, string, string.equalsIgnoreCase(noPrefixCmd));return string.equalsIgnoreCase(noPrefixCmd);}))
        && !noPrefixCmd.isEmpty();

      System.out.println("FOUND CMDOTHER? " + validCmd);

      if(validCmd)
        return Mono.empty();
      else
        return event.getMessage().getChannel()
          .flatMap(channel -> channel.createMessage(String.format("Invalid %s command: '%s'", BubbleBot.cmdPrefix, event.getMessage().getContent()))).then();
    });
  }
}
