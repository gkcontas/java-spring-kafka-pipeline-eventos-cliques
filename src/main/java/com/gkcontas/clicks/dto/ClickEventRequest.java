package com.gkcontas.clicks.dto;

import jakarta.validation.constraints.NotBlank;

public record ClickEventRequest(

        @NotBlank(message = "page is required")
        String page,

        @NotBlank(message = "userId is required")
        String userId
) {
}
