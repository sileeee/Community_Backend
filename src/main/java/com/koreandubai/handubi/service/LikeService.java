package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.LikeRequestDto;
import com.koreandubai.handubi.domain.Like;
import com.koreandubai.handubi.global.common.LikeType;
import com.koreandubai.handubi.repository.LikeRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserService userService;

    @Transactional
    public void toggleLike(HttpServletRequest request, LikeRequestDto likeRequestDto) {

        Long userId = userService.getUserIdFromSession(request);
        Long postId = likeRequestDto.getPostId();
        LikeType likeType = likeRequestDto.getLikeType();

        likeRepository.findByPostIdAndUserId(postId, userId).ifPresentOrElse(likeRepository::delete, () -> {
            Like newLike = Like.builder()
                    .postId(postId)
                    .likeType(likeType)
                    .userId(userId)
                    .build();

            likeRepository.save(newLike);
        });
    }

    public long getLikeCount(Long postId) {

        return likeRepository.countByPostId(postId);
    }

    public boolean isUserLiked(HttpServletRequest request, Long postId) {

        Long userId = userService.getUserIdFromSession(request);

        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }
}

