package com.unir.papertales.catalogue.controller.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String details;
}
