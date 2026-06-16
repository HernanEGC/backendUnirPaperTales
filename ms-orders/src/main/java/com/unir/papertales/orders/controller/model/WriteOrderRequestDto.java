package com.unir.papertales.orders.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteOrderRequestDto implements Serializable {

    @JsonProperty("userId")
    @NotNull
    private Long userId;

    @JsonProperty("orderDate")
    private LocalDate orderDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("items")
    @NotEmpty
    private List<WriteOrderItemDto> items;
}

