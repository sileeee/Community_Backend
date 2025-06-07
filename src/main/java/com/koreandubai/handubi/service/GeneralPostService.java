package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.CreatePostRequestDto;
import com.koreandubai.handubi.controller.dto.DetailedPost;
import com.koreandubai.handubi.controller.dto.EditPostRequestDto;
import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.*;
import com.koreandubai.handubi.global.exception.UnauthorizedException;
import com.koreandubai.handubi.global.util.RedisUtil;
import com.koreandubai.handubi.repository.LikeRepository;
import com.koreandubai.handubi.repository.PostRepository;
import com.koreandubai.handubi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static com.koreandubai.handubi.global.common.PageSize.NOMAL_PAGE_SIZE;


@Service
public class GeneralPostService extends AbstractPostService<DetailedPost, CreatePostRequestDto, EditPostRequestDto>{

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;

    public GeneralPostService(RedisUtil redisUtil, PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository, UserService userService) {
        super(redisUtil);
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    @Override
    public List<DetailedPost> getPosts(CategoryType category, SubCategoryType subCategory, int pageNo, String criteria, PostType postType){

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<Post> posts;
        if(subCategory.equals(SubCategoryType.TOTAL)){
            if (postType != null) {
                posts = postRepository.findAllByCategoryAndPostStatusAndPostType(category, PostStatus.PUBLIC, postType, pageable).getContent();
            } else {
                posts = postRepository.findAllByCategoryAndPostStatus(category, PostStatus.PUBLIC, pageable).getContent();
            }
        } else {
            if (postType == null) {
                posts = postRepository.findAllByCategoryAndSubCategoryAndPostStatus(
                        category, subCategory, PostStatus.PUBLIC, pageable
                ).getContent();

            } else {
                posts = postRepository.findAllByCategoryAndSubCategoryAndPostStatusAndPostType(
                        category, subCategory, PostStatus.PUBLIC, postType, pageable
                ).getContent();

            }
        }

        List<String> userNames = new ArrayList<>();
        List<Long> likes = new ArrayList<>();
        for (Post post : posts) {
            Optional<User> user = userRepository.findById(post.getUserId());
            long like = likeRepository.countByPostId(post.getId());
            if(user.isEmpty()) {
                throw new EntityNotFoundException("User with ID " + post.getUserId() + " not found");
            }
            userNames.add(user.get().getName());
            likes.add(like);
        }

        return DetailedPost.toList(posts, userNames, likes);
    }

    @Override
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
                .postStatus(dto.getPostStatus())
                .postType(dto.getPostType())
                .lastModified(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(HttpServletRequest request, Long postId) {

        Long userId = userService.getUserIdFromSession(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found or not logged in"));

        Optional<Post> deletePost = Optional.ofNullable(postRepository.getPostsById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found")));

        if(!userId.equals(deletePost.get().getUserId()) && user.getUserType() != UserType.ADMIN){
            throw new UnauthorizedException("Post with ID " + postId + " is not owned by user");
        }
        postRepository.deleteById(postId);
    }

    @Override
    @Transactional
    public void editPost(HttpServletRequest request, long postId, EditPostRequestDto dto) {
        Long userId = userService.getUserIdFromSession(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found or not logged in"));

        Post updatePost = postRepository.getPostsById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));

        if (!userId.equals(updatePost.getUserId()) && user.getUserType() != UserType.ADMIN) {
            throw new UnauthorizedException("Post with ID " + postId + " is not owned by user");
        }

        updatePost.setTitle(dto.getTitle());
        updatePost.setBody(dto.getBody());
        updatePost.setSubCategory(dto.getSubCategory());
        updatePost.setPostStatus(dto.getPostStatus());
        updatePost.setLastModified(LocalDateTime.now());
    }

    @Override
    public DetailedPost getSinglePost(long postId) {

        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            throw new EntityNotFoundException("Post with ID " + postId + " not found");
        }

        Optional<User> user = userRepository.findById(post.get().getUserId());
        if(user.isEmpty()){
            throw new EntityNotFoundException("User with ID " + postId + " not found");
        }

        long like = likeRepository.countByPostId(post.get().getId());

        PreventDuplicatedView(user.get().getId(), postId);

        return DetailedPost.builder()
                .id(postId)
                .title(post.get().getTitle())
                .body(post.get().getBody())
                .author(user.get().getName())
                .category(post.get().getCategory())
                .subCategory(post.get().getSubCategory())
                .postStatus(post.get().getPostStatus())
                .createdAt(post.get().getCreatedAt())
                .view(post.get().getView())
                .like(like)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetailedPost> searchPostsByKeyword(@NotBlank String keyword, int pageNo, String criteria) {

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<Post> posts =  postRepository.searchByKeyword(keyword, pageable).getContent();

        List<String> userNames = new ArrayList<>();
        List<Long> likes = new ArrayList<>();
        for (Post post : posts) {
            Optional<User> user = userRepository.findById(post.getUserId());
            long like = likeRepository.countByPostId(post.getId());
            if(user.isEmpty()) {
                throw new EntityNotFoundException("User with ID " + post.getUserId() + " not found");
            }
            userNames.add(user.get().getName());
            likes.add(like);
        }

        return DetailedPost.toList(posts, userNames, likes);
    }


    public void IncreaseViewCount(Long postId){

        Optional<Post> updatePost = postRepository.getPostsById(postId);

        updatePost.ifPresent(selectPost -> {
            selectPost.setView(selectPost.getView() + 1);
            postRepository.save(selectPost);
        });
    }

    @Override
    public List<DetailedPost> getMyPosts(HttpServletRequest request, CategoryType category, int pageNo, String criteria){

        Long userId = userService.getUserIdFromSession(request);
        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<Post> posts;
        if (category == null) {
            posts = postRepository.findAllByUserId(userId, pageable).getContent();
        }else {
            posts = postRepository.findAllByCategoryAndUserId(category, userId, pageable).getContent();
        }

        List<String> userNames = new ArrayList<>();
        List<Long> likes = new ArrayList<>();
        for (Post post : posts) {
            Optional<User> user = userRepository.findById(post.getUserId());
            long like = likeRepository.countByPostId(post.getId());
            if(user.isEmpty()) {
                throw new EntityNotFoundException("User with ID " + post.getUserId() + " not found");
            }
            userNames.add(user.get().getName());
            likes.add(like);
        }

        return DetailedPost.toList(posts, userNames, likes);
    }
}