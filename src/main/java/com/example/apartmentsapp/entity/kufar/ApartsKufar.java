package com.example.apartmentsapp.entity.kufar;

import com.example.apartmentsapp.entity.catalog.Flat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApartsKufar {
  @JsonProperty("ads")
  public List<Ad> ads;
}
