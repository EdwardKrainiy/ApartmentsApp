package com.example.apartmentsapp;

import com.example.apartmentsapp.entity.catalog.ApartmentsObj;
import com.example.apartmentsapp.entity.catalog.Flat;
import com.example.apartmentsapp.entity.kufar.Ad;
import com.example.apartmentsapp.entity.kufar.ApartsKufar;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

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

  public static void main(String[] args) throws IOException, LoginException, InterruptedException {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        System.out.println("Shutting down by shutdown hook");
      }
    });
    JDA api =
        JDABuilder.createDefault(new String(Base64.getDecoder().decode(BOT_TOKEN)))
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .build();

    methodToGetFlat(api);
  }

  private static void methodToGetFlat(JDA api) {
    Runnable debug = () -> {
      api.getTextChannelById(DEBUG_CHANNEL_ID).sendMessage("Works!").queue();
    };

    Runnable getFlatRunnable =
        () -> {
          api.getTextChannelById(CHANNEL_ID).sendMessage("Works!");
          URL url_catalog = null;
          URL url_kufar = null;
          try {
            url_catalog = new URL(CATALOG_ONLINER_1_AND_2_ROOM_FLAT_BEFORE_210_USD);
            url_kufar = new URL(KUFAR_1_AND_2_ROOM_FLAT_BEFORE_250_USD);
          } catch (MalformedURLException e) {
            e.printStackTrace();
            methodToGetFlat(api);
          }

          HttpURLConnection connection_catalog = null;
          HttpURLConnection connection_kufar = null;
          try {
            connection_catalog = (HttpURLConnection) url_catalog.openConnection();
            connection_kufar = (HttpURLConnection) url_kufar.openConnection();
          } catch (IOException e) {
            e.printStackTrace();
            methodToGetFlat(api);
          }

          connection_catalog.setRequestProperty("accept", "application/json");
          connection_kufar.setRequestProperty("accept", "application/json");

          InputStream responseStreamCatalog = null;
          InputStream responseStreamKufar = null;
          try {
            responseStreamCatalog = connection_catalog.getInputStream();
            responseStreamKufar = connection_kufar.getInputStream();
          } catch (IOException e) {
            e.printStackTrace();
            methodToGetFlat(api);
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
            methodToGetFlat(api);
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
            methodToGetFlat(api);
          }

          String responseCatalog = textBuilderCatalog.toString();
          String responseKufar = textBuilderKufar.toString();

          ObjectMapper mapper = new ObjectMapper();
          ApartmentsObj apartsCatalog = null;
          try {
            apartsCatalog = mapper.readValue(responseCatalog, ApartmentsObj.class);
          } catch (JsonProcessingException e) {
            e.printStackTrace();
            methodToGetFlat(api);
          }

          ApartsKufar apartsKufar = null;
          try {
            apartsKufar = mapper.readValue(responseKufar, ApartsKufar.class);
          } catch (JsonProcessingException e) {
            e.printStackTrace();
            methodToGetFlat(api);
          }

          lastFlatCatalog = apartsCatalog.getFlats().get(0);
          TextChannel textChannel = api.getTextChannelById(CHANNEL_ID);
          api.getTextChannelById(DEBUG_CHANNEL_ID).sendMessage("Prev apart: " + prevFlatCatalog.toString());
          api.getTextChannelById(DEBUG_CHANNEL_ID).sendMessage("Last apart: " + lastFlatCatalog.toString());
          if (lastFlatCatalog.getCreatedAt().after(prevFlatCatalog.getCreatedAt())
              || lastFlatCatalog.getLastTimeUp().after(prevFlatCatalog.getLastTimeUp())) {
            textChannel
                .sendMessage(
                    String.format(
                        CONST_MESSAGES.NEW_FLAT_MESSAGE_CATALOG,
                        lastFlatCatalog.getPrice().amount,
                        lastFlatCatalog.getFlatUrl()))
                .queue();
          }
          prevFlatCatalog = lastFlatCatalog;

          lastFlatKufar = apartsKufar.getAds().get(0);
          if (lastFlatKufar.getAdTime().after(prevFlatKufar.getAdTime())) {
            textChannel
                .sendMessage(
                    String.format(
                        CONST_MESSAGES.NEW_FLAT_MESSAGE_KUFAR,
                        lastFlatKufar.getPriceUsd(),
                        lastFlatKufar.getPriceByn(),
                        lastFlatKufar.getAdLink()))
                .queue();
          }
          prevFlatKufar = lastFlatKufar;
          methodToGetFlat(api);
        };

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleAtFixedRate(getFlatRunnable, 5, 5, TimeUnit.SECONDS);
    executor.scheduleAtFixedRate(debug, 5, 10, TimeUnit.SECONDS);
  }
}
