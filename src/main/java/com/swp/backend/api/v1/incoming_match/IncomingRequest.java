package com.swp.backend.api.v1.incoming_match;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class IncomingRequest
{
    private int itemsPerPage;
    private int page;
}
