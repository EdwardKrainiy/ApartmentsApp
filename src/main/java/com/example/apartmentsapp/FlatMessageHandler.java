package com.example.apartmentsapp;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class FlatMessageHandler extends ListenerAdapter {
  private final JDA currentJdaObject;

  public FlatMessageHandler(JDA currentJdaObject) {
    this.currentJdaObject = currentJdaObject;
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    Message msg = event.getMessage();
    MessageChannel messageChannel = currentJdaObject.getTextChannelById("967098054639497266");
    messageChannel.sendMessage("Hello");
  }
}
