package com.unir.papertales.orders.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteOrderItemDto implements Serializable {

    @JsonProperty("bookId")
    @NotNull
    private Long bookId;

    @JsonProperty("price")
    @NotNull
    private Double price;

    @JsonProperty("quantity")
    @Min(1)
    @Builder.Default
    private Integer quantity = 1;
}

