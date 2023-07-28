package com.swp.backend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageBean {
    private String from;
    private String message;
    private List<String> to;
}