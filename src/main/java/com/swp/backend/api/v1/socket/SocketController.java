package com.swp.backend.api.v1.socket;

import com.swp.backend.entity.BookingEntity;
import com.swp.backend.model.MessageBean;
import com.swp.backend.model.WebsocketCancelBookingMessage;
import com.swp.backend.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
@AllArgsConstructor
public class SocketController {
    private SimpMessagingTemplate simpMessagingTemplate;
    private BookingService bookingService;

    @MessageMapping("/user-all")
    @SendTo("/topic/user-all")
    public MessageBean send(@Payload MessageBean message) {
        System.out.println("Reached");
        return message;
    }

    @MessageMapping("/specific-user")
    public void simple(@Payload MessageBean message){
        System.out.println("Check in");
        String id = message.getMessage();
        System.out.println(id);
        message.setMessage("Caught");
        simpMessagingTemplate.convertAndSend("/topic/specific-user/" + id, message);
    }

    @MessageMapping("/owner/cancel-booking")
    public void OwnerCancelBooking(@Payload WebsocketCancelBookingMessage message){
        System.out.println("Check in Owner Cancel Booking controller");
        BookingEntity entity = bookingService.getBookingById(message.getBookingId());
        String userId = entity.getAccountId();
        simpMessagingTemplate.convertAndSend("/topic/specific-user/" + userId, "Your booking is just canceled by owner, please check mail to see detail");
    }
}