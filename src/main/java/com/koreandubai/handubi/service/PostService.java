package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.CreatePostRequestDto;
import com.koreandubai.handubi.controller.dto.DetailedPost;
import com.koreandubai.handubi.controller.dto.EditPostRequestDto;
import com.koreandubai.handubi.controller.dto.SimplePost;
import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.SessionKey;
import com.koreandubai.handubi.global.exception.UnauthorizedException;
import com.koreandubai.handubi.global.util.RedisUtil;
import com.koreandubai.handubi.repository.PostRepository;
import com.koreandubai.handubi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.koreandubai.handubi.global.common.PageSize.NOMAL_PAGE_SIZE;


@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    public List<SimplePost> getPosts(CategoryType category, int pageNo, String criteria){

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<Post> posts = postRepository.findAllByCategory(category, pageable).getContent();

        List<String> userNames = new ArrayList<>();
        for (Post post : posts) {
            Optional<User> user = userRepository.findById(post.getUserId());
            if(user.isEmpty()) {
                throw new EntityNotFoundException("User with ID " + post.getUserId() + " not found");
            }
            userNames.add(user.get().getName());
        }

        return SimplePost.toList(posts, userNames);
    }

    public void createPost(HttpServletRequest request, CategoryType category, CreatePostRequestDto dto) {

        HttpSession session = request.getSession();
        Long userId = (Long) Optional.ofNullable(session.getAttribute(SessionKey.LOGIN_USER_ID)).orElseThrow(UnauthorizedException::new);

        Post post = Post.builder()
                .category(category)
                .title(dto.getTitle())
                .body(dto.getBody())
                .userId(userId)
                .view(0L)
                .status(dto.getStatus())
                .build();

        postRepository.save(post);
    }

    public void deletePost(HttpServletRequest request, Long postId) {

        HttpSession session = request.getSession();
        Long userId = (Long) Optional.ofNullable(session.getAttribute(SessionKey.LOGIN_USER_ID)).orElseThrow(UnauthorizedException::new);

        if(postRepository.getPostsById(postId).isEmpty()){
            throw new EntityNotFoundException("Post with ID " + postId + " not found");
        }

        if(!userId.equals(postRepository.getPostsById(postId).get().getUserId())){
            throw new UnauthorizedException("Post with ID " + postId + " is not owned by user");
        }
        postRepository.deleteById(postId);
    }

    public void editPost(HttpServletRequest request, long postId, EditPostRequestDto dto) {

        HttpSession session = request.getSession();
        Long userId = (Long) Optional.ofNullable(session.getAttribute(SessionKey.LOGIN_USER_ID)).orElseThrow(UnauthorizedException::new);

        if(postRepository.getPostsById(postId).isEmpty()){
            throw new EntityNotFoundException("Post with ID " + postId + " not found");
        }

        if(!userId.equals(postRepository.getPostsById(postId).get().getUserId())){
            throw new UnauthorizedException("Post with ID " + postId + " is not owned by user");
        }

        Optional<Post> updatePost = postRepository.getPostsById(postId);

        updatePost.ifPresent(selectPost-> {
            selectPost.setTitle(dto.getTitle());
            selectPost.setBody(dto.getBody());
            selectPost.setStatus(dto.getStatus());
            selectPost.setLastModified(LocalDateTime.now());

            postRepository.save(selectPost);
        });
    }

    public void IncreaseViewCount(Long userId, Long postId) {

        String viewCount = redisUtil.getData(String.valueOf(userId));
        if (viewCount == null) {
            redisUtil.setDateExpire(String.valueOf(userId), postId + "_", calculateTimeUntilMidnight());
//            post.increaseView();
        } else {
            String[] strArray = viewCount.split("_");
            List<String> redisPostList = Arrays.asList(strArray);

            boolean isView = false;

            if (!redisPostList.isEmpty()) {
                for (String redisPortfolioId : redisPostList) {
                    if (String.valueOf(postId).equals(redisPortfolioId)) {
                        isView = true;
                        break;
                    }
                }
                if (!isView) {
                    viewCount += postId + "_";

                    redisUtil.setDateExpire(String.valueOf(userId), viewCount, calculateTimeUntilMidnight());
//                    post.updateView();
                }
            }
        }
    }

    public static long calculateTimeUntilMidnight() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS).plusDays(1);
        return ChronoUnit.SECONDS.between(now, midnight);
    }

    public DetailedPost getSinglePost(long postId) {

        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            throw new EntityNotFoundException("Post with ID " + postId + " not found");
        }

        Optional<User> user = userRepository.findById(post.get().getUserId());
        if(user.isEmpty()){
            throw new EntityNotFoundException("User with ID " + postId + " not found");
        }

        return DetailedPost.builder()
                .title(post.get().getTitle())
                .body(post.get().getBody())
                .author(user.get().getName())
                .createdAt(post.get().getCreatedAt())
                .view(post.get().getView())
                .build();
    }
}