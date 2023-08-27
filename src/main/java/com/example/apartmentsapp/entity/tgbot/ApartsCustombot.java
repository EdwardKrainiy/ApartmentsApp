package com.example.apartmentsapp.entity.tgbot;

import java.util.Base64;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ApartsCustombot extends TelegramLongPollingBot {
  private static final String TG_BOT_TOKEN =
      "6673838632:AAFr6djD9hzYkR9EYwYBBcOWN8MbfarWgs0";

  @Override
  public void onUpdateReceived(Update update) {}

  @Override
  public String getBotUsername() {
    return "ApartmentsBot";
  }

  @Override
  public String getBotToken() {
    return TG_BOT_TOKEN;
  }
}
