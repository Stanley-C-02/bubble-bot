package com.bubblebot.main;

import java.util.Arrays;
import java.util.function.Consumer;

public class Command {
  private String name;
  private String[] alias;
  Consumer<?> func;

  public Command(final String name, final String[] alias, final Consumer<?> func) {
    this.name = name;
    this.alias = alias;
    this.func = func;
  }
}
