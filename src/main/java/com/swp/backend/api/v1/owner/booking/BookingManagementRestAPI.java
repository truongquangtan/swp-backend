package com.swp.backend.api.v1.owner.booking;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/owner")
public class BookingManagementRestAPI {
    @GetMapping(value = "booking")
    public ResponseEntity<String> showAllBooking(){
        return ResponseEntity.ok().build();
    }
}