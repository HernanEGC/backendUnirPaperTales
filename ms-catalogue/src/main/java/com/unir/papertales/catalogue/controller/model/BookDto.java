package com.unir.papertales.catalogue.controller.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "title",
        "author",
        "code",
        "category",
        "description",
        "price",
        "rating",
        "stock",
        "publication_date",
        "image_url",
        "visible"
})
@Getter
@Setter
@Builder
public class BookDto implements Serializable {

    private static final long serialVersionUID = 1901178943784643027L;

    @JsonProperty("id")
    public Long id;
    @JsonProperty("title")
    public String title;
    @JsonProperty("author")
    public String author;
    @JsonProperty("code")
    public String code;
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
    @JsonProperty("publication_date")
    public LocalDate publicationDate;
    @JsonProperty("image_url")
    public String imageUrl;
    @JsonProperty("visible")
    public Boolean visible;




}
