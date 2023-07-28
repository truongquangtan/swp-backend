package com.swp.backend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebsocketCancelBookingMessage {
    private String bookingId;
}
