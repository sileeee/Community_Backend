package com.koreandubai.handubi.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPointRequestDto {

    private Long userId;

    private Long actionTypeId;

    private Integer points;

    private String referenceNote;

    private Long referencePostId;

    private Long referenceCommentId;
}

