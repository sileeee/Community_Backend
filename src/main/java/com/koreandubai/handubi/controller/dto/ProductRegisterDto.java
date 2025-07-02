package com.koreandubai.handubi.controller.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductRegisterDto {
    private String name;
    private String description;
    private int pointPrice;
    private int stockQty;
    private LocalDateTime deadline;
}
