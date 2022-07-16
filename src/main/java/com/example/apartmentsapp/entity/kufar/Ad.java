package com.example.apartmentsapp.entity.kufar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ad {
  @JsonProperty("ad_link")
  public String adLink;

  @JsonProperty("list_time")
  public Date adTime;

  @JsonProperty("price_byn")
  public double priceByn;

  @JsonProperty("price_usd")
  public double priceUsd;

  public Ad(){
    adTime = new Date();
  }

}
