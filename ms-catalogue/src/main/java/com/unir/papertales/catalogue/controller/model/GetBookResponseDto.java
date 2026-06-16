package com.unir.papertales.catalogue.controller.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "title",
        "author",
        "publication_date",
        "description",
        "category",
        "price",
        "stock",
        "rating",
        "image_url",
        "visible",
        "specifications",
        "images"
    })

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetBookResponseDto implements Serializable {

    private static final long serialVersionUID = 7686450847709803303L; // NOSONAR

    @JsonProperty("id")
    public Long id;

    @JsonProperty("title")
    public String title;

    @JsonProperty("code")
    public String code;
    @JsonProperty("author")
    public String author;

    @JsonProperty("publication_date")
    public LocalDate publicationDate;

    @JsonProperty("description")
    public String description;

    @JsonProperty("category")
    public String category;

    @JsonProperty("price")
    public Double price;

    @JsonProperty("stock")
    public Integer stock;

    @JsonProperty("rating")
    public Integer rating;

    @JsonProperty("image_url")
    public String imageUrl;

    @JsonProperty("visible")
    public Boolean visible;

    @JsonProperty("images")
    public String images;
}
