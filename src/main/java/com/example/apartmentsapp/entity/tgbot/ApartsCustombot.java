package com.example.apartmentsapp.entity.tgbot;

import java.util.Base64;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ApartsCustombot extends TelegramLongPollingBot {
  private static final String TG_BOT_TOKEN =
      "NTM3ODA2OTE0NDpBQUh1ajhTUFNjbUpSS0JlNFMxSjVSUGxiRk5ibm12UGgyaw==";

  @Override
  public void onUpdateReceived(Update update) {}

  @Override
  public String getBotUsername() {
    return "ApartmentsBot";
  }

  @Override
  public String getBotToken() {
    return new String(Base64.getDecoder().decode(TG_BOT_TOKEN));
  }
}
