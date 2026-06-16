package com.unir.papertales.orders.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class OrderDto implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("orderDate")
    private LocalDate orderDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("total")
    private Double total;

    @JsonProperty("items")
    private List<OrderItemDto> items;
}

