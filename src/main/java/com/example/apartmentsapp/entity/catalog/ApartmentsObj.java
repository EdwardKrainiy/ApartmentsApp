package com.example.apartmentsapp.entity.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApartmentsObj {
  @JsonProperty("apartments")
  public List<Flat> flats;
}
