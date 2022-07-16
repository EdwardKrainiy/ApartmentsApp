package com.example.apartmentsapp;

import com.example.apartmentsapp.entity.catalog.ApartmentsObj;
import com.example.apartmentsapp.entity.catalog.Flat;
import com.example.apartmentsapp.entity.kufar.Ad;
import com.example.apartmentsapp.entity.kufar.ApartsKufar;
import com.example.apartmentsapp.entity.tgbot.ApartsCustombot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class ApartmentsAppApplication extends ListenerAdapter {
  public static final String BOT_TOKEN =
      "T1RZM01EazBOakk0TmpJeU9USTFPVEk1LkdRLWwzcS5JcWs4LW1UTVlSRnpid0JjQzFHUEV1NGpoa3dJUmMwcUNmVmJfSQ==";

  public static String CATALOG_ONLINER_1_AND_2_ROOM_FLAT_BEFORE_210_USD =
      "https://r.onliner.by/sdapi/ak.api/search/apartments?rent_type%5B%5D=1_room&rent_type%5B%5D=2_rooms&price%5Bmin%5D=50&price%5Bmax%5D=220&currency=usd&only_owner=true&bounds%5Blb%5D%5Blat%5D=53.86791485688156&bounds%5Blb%5D%5Blong%5D=27.301025390625004&bounds%5Brt%5D%5Blat%5D=53.928602727199326&bounds%5Brt%5D%5Blong%5D=27.822875976562504&v=0.5573897113511816";
  public static String KUFAR_1_AND_2_ROOM_FLAT_BEFORE_250_USD =
      "https://cre-api-v2.kufar.by/items-search/v1/engine/v1/search/rendered-paginated?cat=1010&cmp=0&cur=USD&gbx=b%3A25.939473597656242%2C53.756079259392074%2C29.51552340234373%2C54.00922553919993&gtsy=country-belarus~province-minsk~locality-minsk&lang=ru&oph=1&prc=r%3A0%2C250&rms=v.or%3A1%2C2&rnt=1&size=30&typ=let";
  public static String CHANNEL_ID = "997596106731048960";
  public static String DEBUG_CHANNEL_ID = "997626374510612522";
  public static Flat prevFlatCatalog = new Flat();
  public static Flat lastFlatCatalog = new Flat();

  public static Ad prevFlatKufar = new Ad();
  public static Ad lastFlatKufar = new Ad();

  private static void methodToGetFlat(JDA api, ApartsCustombot apartsCustombot)
      throws TelegramApiException {
    api.getTextChannelById(DEBUG_CHANNEL_ID).sendMessage("Works!").queue();

    URL url_catalog = null;
    try {
      url_catalog = new URL(CATALOG_ONLINER_1_AND_2_ROOM_FLAT_BEFORE_210_USD);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      methodToGetFlat(api, apartsCustombot);
    }

    HttpURLConnection connection_catalog = null;
    try {
      connection_catalog = (HttpURLConnection) url_catalog.openConnection();
    } catch (IOException e) {
      e.printStackTrace();
      methodToGetFlat(api, apartsCustombot);
    }

    connection_catalog.setRequestProperty("accept", "application/json");

    InputStream responseStreamCatalog = null;
    try {
      responseStreamCatalog = connection_catalog.getInputStream();
    } catch (IOException e) {
      e.printStackTrace();
      methodToGetFlat(api, apartsCustombot);
    }
    StringBuilder textBuilderCatalog = new StringBuilder();
    try (Reader reader =
        new BufferedReader(
            new InputStreamReader(
                responseStreamCatalog, Charset.forName(StandardCharsets.UTF_8.name())))) {
      int c = 0;
      while ((c = reader.read()) != -1) {
        textBuilderCatalog.append((char) c);
      }
    } catch (IOException e) {
      e.printStackTrace();
      methodToGetFlat(api, apartsCustombot);
    }

    String responseCatalog = textBuilderCatalog.toString();

    ObjectMapper mapper = new ObjectMapper();
    ApartmentsObj apartsCatalog = null;
    try {
      apartsCatalog = mapper.readValue(responseCatalog, ApartmentsObj.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      methodToGetFlat(api,apartsCustombot);
    }

    lastFlatCatalog = apartsCatalog.getFlats().get(0);
    TextChannel textChannel = api.getTextChannelById(CHANNEL_ID);
    if (lastFlatCatalog.getCreatedAt().after(prevFlatCatalog.getCreatedAt())
        || lastFlatCatalog.getLastTimeUp().after(prevFlatCatalog.getLastTimeUp())) {
      textChannel
          .sendMessage(
              String.format(
                  CONST_MESSAGES.NEW_FLAT_MESSAGE_CATALOG,
                  lastFlatCatalog.getPrice().amount,
                  lastFlatCatalog.getFlatUrl()))
          .queue();
      SendMessage answer = new SendMessage();
      answer.setText(String.format(
          CONST_MESSAGES.NEW_FLAT_MESSAGE_CATALOG,
          lastFlatCatalog.getPrice().amount,
          lastFlatCatalog.getFlatUrl()));
      answer.setChatId("772207837");
      apartsCustombot.execute(answer);
    }
    prevFlatCatalog = lastFlatCatalog;

    URL url_kufar = null;
    try {
      url_kufar = new URL(KUFAR_1_AND_2_ROOM_FLAT_BEFORE_250_USD);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      methodToGetFlat(api, apartsCustombot);
    }

    HttpURLConnection connection_kufar = null;
    try {
      connection_kufar = (HttpURLConnection) url_kufar.openConnection();
    } catch (IOException e) {
      e.printStackTrace();
      methodToGetFlat(api, apartsCustombot);
    }

    connection_kufar.setRequestProperty("accept", "application/json");

    InputStream responseStreamKufar = null;
    try {
      responseStreamKufar = connection_kufar.getInputStream();
    } catch (IOException e) {
      e.printStackTrace();
      methodToGetFlat(api,apartsCustombot);
    }

    StringBuilder textBuilderKufar = new StringBuilder();
    try (Reader reader =
        new BufferedReader(
            new InputStreamReader(
                responseStreamKufar, Charset.forName(StandardCharsets.UTF_8.name())))) {
      int c = 0;
      while ((c = reader.read()) != -1) {
        textBuilderKufar.append((char) c);
      }
    } catch (IOException e) {
      e.printStackTrace();
      methodToGetFlat(api, apartsCustombot);
    }

    String responseKufar = textBuilderKufar.toString();

    ObjectMapper mapper2 = new ObjectMapper();

    ApartsKufar apartsKufar = null;
    try {
      apartsKufar = mapper2.readValue(responseKufar, ApartsKufar.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      methodToGetFlat(api, apartsCustombot);
    }

    lastFlatKufar = apartsKufar.getAds().get(0);
    if (lastFlatKufar.getAdTime().after(prevFlatKufar.getAdTime())) {
      textChannel
          .sendMessage(
              String.format(
                  CONST_MESSAGES.NEW_FLAT_MESSAGE_KUFAR,
                  lastFlatKufar.getPriceUsd() / 100,
                  lastFlatKufar.getPriceByn() / 100,
                  lastFlatKufar.getAdLink()))
          .queue();
      SendMessage answer = new SendMessage();
      answer.setText(String.format(
          CONST_MESSAGES.NEW_FLAT_MESSAGE_KUFAR,
          lastFlatKufar.getPriceUsd() / 100,
          lastFlatKufar.getPriceByn() / 100,
          lastFlatKufar.getAdLink()));
      answer.setChatId("772207837");
      apartsCustombot.execute(answer);
    }
    prevFlatKufar = lastFlatKufar;
  }

  public static void main(String[] args)
      throws IOException, LoginException, InterruptedException, TelegramApiException {

    JDA api =
        JDABuilder.createDefault(new String(Base64.getDecoder().decode(BOT_TOKEN)))
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .build();

    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    ApartsCustombot apartsCustombot = new ApartsCustombot();
    botsApi.registerBot(apartsCustombot);

    Timer timer = new Timer();
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            try {
              methodToGetFlat(api, apartsCustombot);
            } catch (TelegramApiException e) {
              e.printStackTrace();
            }
          }
        },
        5000,
        5000);
  }
}
