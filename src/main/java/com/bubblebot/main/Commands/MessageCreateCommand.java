package com.bubblebot.main.Commands;

abstract class MessageCreateCommand implements Command {
  abstract String[] parseCommand();
  abstract String getPrefix();
}
