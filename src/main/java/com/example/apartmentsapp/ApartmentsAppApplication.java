package com.example.apartmentsapp;

import static discord4j.core.object.audit.OptionKey.CHANNEL_ID;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class ApartmentsAppApplication extends ListenerAdapter {

  public static final String BOT_TOKEN =
      "T1RZM01EazBOakk0TmpJeU9USTFPVEk1LkdRLWwzcS5JcWs4LW1UTVlSRnpid0JjQzFHUEV1NGpoa3dJUmMwcUNmVmJfSQ==";
  public static final String TG_CHAT_ID = "-1001865116617";
  public static String CATALOG_ONLINER_1_AND_2_ROOM_FLAT_BEFORE_210_USD = "https://r.onliner"
      + ".by/sdapi/ak.api/search/apartments?price%5Bmin%5D=50&price%5Bmax%5D=350&currency=usd"
      + "&only_owner=true&rent_type%5B%5D=1_room&rent_type%5B%5D=2_rooms&bounds%5Blb%5D%5Blat%5D=53.78657693504207&bounds%5Blb%5D%5Blong%5D=27.435745805680334&bounds%5Brt%5D%5Blat%5D=54.01776624246658&bounds%5Brt%5D%5Blong%5D=27.688539720855868&page=1&v=0.46606547057616043";
  public static String KUFAR_1_AND_2_ROOM_FLAT_BEFORE_250_USD = "https://api.kufar"
      + ".by/search-api/v1/search/rendered-paginated?cat=1010&cmp=0&cur=USD&gtsy=country-belarus"
      + "~province-minsk~locality-minsk&lang=ru&prc=r%3A0%2C350&rms=v"
      + ".or%3A2%2C1&rnt=1&size=30&typ=let";
  public static String DEBUG_CHANNEL_ID = "997626374510612522";
  public static Flat prevFlatCatalog = new Flat();
  public static Flat lastFlatCatalog = new Flat();

  public static Ad prevFlatKufar = new Ad();
  public static Ad lastFlatKufar = new Ad();

  private static void methodToGetFlat(JDA api, ApartsCustombot apartsCustombot)
      throws TelegramApiException {
    api.getTextChannelById(DEBUG_CHANNEL_ID).sendMessage("Works! " + LocalDateTime.now().toString()).queue();

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
      methodToGetFlat(api, apartsCustombot);
    }

    lastFlatCatalog = apartsCatalog.getFlats().get(0);
    if (lastFlatCatalog.getCreatedAt().after(prevFlatCatalog.getCreatedAt())
        || lastFlatCatalog.getLastTimeUp().after(prevFlatCatalog.getLastTimeUp())) {
      SendMessage answer = new SendMessage();
      answer.setText(
          String.format(
              CONST_MESSAGES.NEW_FLAT_MESSAGE_CATALOG,
              lastFlatCatalog.getPrice().amount,
              lastFlatCatalog.getFlatUrl()));
      answer.setChatId(TG_CHAT_ID);
      apartsCustombot.execute(answer);
    }
    lastFlatCatalog = apartsCatalog.getFlats().get(0);
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
      methodToGetFlat(api, apartsCustombot);
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
      SendMessage answer = new SendMessage();
      answer.setText(
          String.format(
              CONST_MESSAGES.NEW_FLAT_MESSAGE_KUFAR,
              lastFlatKufar.getPriceUsd() / 100,
              lastFlatKufar.getPriceByn() / 100,
              lastFlatKufar.getAdLink()));
      answer.setChatId(TG_CHAT_ID);
      apartsCustombot.execute(answer);
    }
    lastFlatKufar = apartsKufar.getAds().get(0);
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
        10000,
        15000);
  }
}
