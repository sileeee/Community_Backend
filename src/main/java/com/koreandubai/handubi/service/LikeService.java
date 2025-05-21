package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.LikeRequestDto;
import com.koreandubai.handubi.controller.dto.TopLikedPostDto;
import com.koreandubai.handubi.domain.Like;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.LikeType;
import com.koreandubai.handubi.repository.LikeRepository;
import com.koreandubai.handubi.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserService userService;
    private final PostRepository postRepository;

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

    public List<TopLikedPostDto> getTopLikedPosts(CategoryType categoryType, LocalDateTime startDate, LocalDateTime endDate) {

        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        List<Long> postIds = postRepository.findPostIdsByCategory(categoryType);
        if (postIds.isEmpty())
            return Collections.emptyList();

        List<Object[]> results = likeRepository.findTopLikedPosts(postIds, startDate, endDate);

        return results.stream()
                .map(result -> new TopLikedPostDto((Long) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }
}

