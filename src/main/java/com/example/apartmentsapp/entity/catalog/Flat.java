package com.example.apartmentsapp.entity.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flat {
  @JsonProperty("id")
  public String id;

  @JsonProperty("created_at")
  public Date createdAt;

  @JsonProperty("last_time_up")
  public Date lastTimeUp;

  @JsonProperty("url")
  public String flatUrl;

  @JsonProperty("price")
  public Price price;
}
