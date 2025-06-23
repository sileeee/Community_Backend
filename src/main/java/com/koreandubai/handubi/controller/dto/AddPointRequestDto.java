package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.global.common.ActionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPointRequestDto {

    private Long userId;

    private ActionType actionType;

    private Integer points;

    private Long referencePostId;

    private Long referenceCommentId;
}

