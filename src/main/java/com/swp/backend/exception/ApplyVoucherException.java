package com.swp.backend.exception;

import lombok.*;

@Getter
@Setter
@Builder
public class ApplyVoucherException extends Exception{
    private String errorMessage;
    private String stack;
}
