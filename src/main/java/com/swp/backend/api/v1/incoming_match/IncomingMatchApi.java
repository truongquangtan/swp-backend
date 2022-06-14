package com.swp.backend.api.v1.incoming_match;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/me")
@AllArgsConstructor
public class IncomingMatchApi {
    @GetMapping(value = "incoming-matches")
    public ResponseEntity<String> incomingMatch(@RequestBody(required = false) IncomingRequest request)
    {

    }
}
