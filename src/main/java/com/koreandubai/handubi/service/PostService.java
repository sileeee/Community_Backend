package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.CreatePostRequestDto;
import com.koreandubai.handubi.controller.dto.DetailedPost;
import com.koreandubai.handubi.controller.dto.EditPostRequestDto;
import com.koreandubai.handubi.controller.dto.SimplePost;
import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.SubCategoryType;
import com.koreandubai.handubi.global.exception.UnauthorizedException;
import com.koreandubai.handubi.global.util.RedisUtil;
import com.koreandubai.handubi.repository.PostRepository;
import com.koreandubai.handubi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final UserService userService;

    public List<SimplePost> getPosts(CategoryType category, SubCategoryType subCategory, int pageNo, String criteria){

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<Post> posts;
        if(subCategory.equals(SubCategoryType.TOTAL)){
            posts = postRepository.findAllByCategory(category,pageable).getContent();
        }else {
            posts = postRepository.findAllByCategoryAndSubCategory(category, subCategory, pageable).getContent();
        }

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

    @Transactional
    public void createPost(HttpServletRequest request, CategoryType category, CreatePostRequestDto dto) {

        Long userId = userService.getUserIdFromSession(request);

        Post post = Post.builder()
                .category(category)
                .title(dto.getTitle())
                .body(dto.getBody())
                .subCategory(dto.getSubCategory())
                .userId(userId)
                .view(0L)
                .status(dto.getStatus())
                .lastModified(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }

    @Transactional
    public void deletePost(HttpServletRequest request, Long postId) {

        Long userId = userService.getUserIdFromSession(request);

        Optional<Post> deletePost = Optional.ofNullable(postRepository.getPostsById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found")));

        if(!userId.equals(deletePost.get().getUserId())){
            throw new UnauthorizedException("Post with ID " + postId + " is not owned by user");
        }
        postRepository.deleteById(postId);
    }

    @Transactional
    public void editPost(HttpServletRequest request, long postId, EditPostRequestDto dto) {

        Long userId = userService.getUserIdFromSession(request);

        Optional<Post> updatePost = Optional.ofNullable(postRepository.getPostsById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found")));

        if(!userId.equals(updatePost.get().getUserId())){
            throw new UnauthorizedException("Post with ID " + postId + " is not owned by user");
        }

        updatePost.ifPresent(selectPost-> {
            selectPost.setTitle(dto.getTitle());
            selectPost.setBody(dto.getBody());
            selectPost.setSubCategory(dto.getSubCategory());
            selectPost.setStatus(dto.getStatus());
            selectPost.setLastModified(LocalDateTime.now());

            postRepository.save(selectPost);
        });
    }

    public void IncreaseViewCount(Long postId){

        Optional<Post> updatePost = postRepository.getPostsById(postId);

        updatePost.ifPresent(selectPost -> {
            selectPost.setView(selectPost.getView() + 1);
            postRepository.save(selectPost);
        });
    }

    public static long calculateTimeUntilMidnight() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.truncatedTo(ChronoUnit.DAYS).plusDays(1);
        return ChronoUnit.SECONDS.between(now, midnight);
    }

    public void PreventDuplicatedView(Long userId, Long postId) {

        String viewCount = redisUtil.getData(String.valueOf(userId));
        if (viewCount == null) {
            redisUtil.setDateExpire(String.valueOf(userId), postId + "_", calculateTimeUntilMidnight());
            IncreaseViewCount(postId);
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
                    IncreaseViewCount(postId);
                }
            }
        }
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

        PreventDuplicatedView(user.get().getId(), postId);

        return DetailedPost.builder()
                .id(postId)
                .title(post.get().getTitle())
                .body(post.get().getBody())
                .author(user.get().getName())
                .subCategory(post.get().getSubCategory())
                .createdAt(post.get().getCreatedAt())
                .view(post.get().getView())
                .build();
    }

    @Transactional(readOnly = true)
    public List<SimplePost> searchPostsByKeyword(@NotBlank String keyword, int pageNo, String criteria) {

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<Post> posts =  postRepository.searchByKeyword(keyword, pageable).getContent();

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
}