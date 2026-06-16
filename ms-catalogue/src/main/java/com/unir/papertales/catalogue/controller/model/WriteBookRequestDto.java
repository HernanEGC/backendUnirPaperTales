package com.unir.papertales.catalogue.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({

})
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WriteBookRequestDto implements Serializable {

    private static final  long serialVersionUID = 7686450847709803303L;

    @JsonProperty("title")
    public String title;

    @JsonProperty("author")
    public String author;

    @JsonProperty("code")
    public String code;

    @JsonProperty("publication_date")
    public LocalDate publicationDate;

    @JsonProperty("category")
    public String category;

    @JsonProperty("price")
    public BigDecimal price;

    @JsonProperty("rating")
    public Integer rating;

    @JsonProperty("visible")
    public Boolean visible;

    @JsonProperty("stock")
    public Integer stock;

    @JsonProperty("image_url")
    public String imageUrl;

    @JsonProperty("description")
    public String description;
}
