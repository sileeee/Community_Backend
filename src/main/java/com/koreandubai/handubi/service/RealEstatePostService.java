package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.CreateRealEstatePostRequestDto;
import com.koreandubai.handubi.controller.dto.DetailedRealEstatePost;
import com.koreandubai.handubi.controller.dto.EditRealEstatePostRequestDto;
import com.koreandubai.handubi.domain.RealEstatePost;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.SubCategoryType;
import com.koreandubai.handubi.global.exception.UnauthorizedException;
import com.koreandubai.handubi.global.util.RedisUtil;
import com.koreandubai.handubi.repository.LikeRepository;
import com.koreandubai.handubi.repository.RealEstateRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.koreandubai.handubi.global.common.PageSize.NOMAL_PAGE_SIZE;

@Service
public class RealEstatePostService extends AbstractPostService<DetailedRealEstatePost, CreateRealEstatePostRequestDto, EditRealEstatePostRequestDto> {

    private final RealEstateRepository realEstateRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;

    public RealEstatePostService(RedisUtil redisUtil, RealEstateRepository realEstateRepository, UserRepository userRepository, LikeRepository likeRepository, UserService userService, RealEstateRepository realEstateRepository1) {
        super(redisUtil);
        this.realEstateRepository = realEstateRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    @Override
    public List<DetailedRealEstatePost> getPosts(CategoryType category, SubCategoryType subCategory, int pageNo, String criteria){

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<RealEstatePost> posts;
        if(subCategory.equals(SubCategoryType.TOTAL)){
            posts = realEstateRepository.findAllByPostStatus(PostStatus.PUBLIC, pageable).getContent();
        }else {
            posts = realEstateRepository.findAllBySubCategoryAndPostStatus(subCategory, PostStatus.PUBLIC, pageable).getContent();
        }

        List<String> userNames = new ArrayList<>();
        List<Long> likes = new ArrayList<>();
        for (RealEstatePost post : posts) {
            Optional<User> user = userRepository.findById(post.getUserId());
            long like = likeRepository.countByPostId(post.getId());
            if(user.isEmpty()) {
                throw new EntityNotFoundException("User with ID " + post.getUserId() + " not found");
            }
            userNames.add(user.get().getName());
            likes.add(like);
        }

        return DetailedRealEstatePost.toList(posts, userNames, likes);
    }


    @Override
    public DetailedRealEstatePost getSinglePost(long postId) {

        Optional<RealEstatePost> post = realEstateRepository.findById(postId);
        if(post.isEmpty()){
            throw new EntityNotFoundException("Post with ID " + postId + " not found");
        }

        Optional<User> user = userRepository.findById(post.get().getUserId());
        if(user.isEmpty()){
            throw new EntityNotFoundException("User with ID " + postId + " not found");
        }

        long like = likeRepository.countByPostId(post.get().getId());

        PreventDuplicatedView(user.get().getId(), postId);

        return DetailedRealEstatePost.builder()
                .id(postId)
                .title(post.get().getTitle())
                .body(post.get().getBody())
                .author(user.get().getName())
                .subCategory(post.get().getSubCategory())
                .productType(post.get().getProductType())
                .postStatus(post.get().getPostStatus())
                .view(post.get().getView())
                .thumbnailUrl(post.get().getThumbnailUrl())
                .innerArea(post.get().getInnerArea())
                .totalArea(post.get().getTotalArea())
                .state(post.get().getState())
                .price(post.get().getPrice())
                .createdAt(post.get().getCreatedAt())
                .productStatus(post.get().getProductStatus())
                .like(like)
                .build();
    }


    @Override
    @Transactional
    public void createPost(HttpServletRequest request, CategoryType category, CreateRealEstatePostRequestDto dto) {

        Long userId = userService.getUserIdFromSession(request);

        RealEstatePost post = RealEstatePost.builder()
                .subCategory(dto.getSubCategory())
                .title(dto.getTitle())
                .body(dto.getBody())
                .subCategory(dto.getSubCategory())
                .productType(dto.getProductType())
                .userId(userId)
                .postStatus(dto.getPostStatus())
                .view(0L)
                .thumbnailUrl(dto.getThumbnailUrl())
                .postStatus(dto.getPostStatus())
                .productStatus(dto.getProductStatus())
                .innerArea(dto.getInnerArea())
                .totalArea(dto.getTotalArea())
                .state(dto.getState())
                .price(dto.getPrice())
                .lastModified(LocalDateTime.now())
                .build();

        realEstateRepository.save(post);
    }


    @Override
    @Transactional
    public void deletePost(HttpServletRequest request, Long postId) {

        Long userId = userService.getUserIdFromSession(request);

        Optional<RealEstatePost> deletePost = Optional.ofNullable(realEstateRepository.getPostsById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found")));

        if(!userId.equals(deletePost.get().getUserId())){
            throw new UnauthorizedException("Post with ID " + postId + " is not owned by user");
        }
        realEstateRepository.deleteById(postId);
    }


    @Override
    @Transactional
    public void editPost(HttpServletRequest request, long postId, EditRealEstatePostRequestDto dto) {

        Long userId = userService.getUserIdFromSession(request);

        Optional<RealEstatePost> updatePost = Optional.ofNullable(realEstateRepository.getPostsById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found")));

        if(!userId.equals(updatePost.get().getUserId())){
            throw new UnauthorizedException("Post with ID " + postId + " is not owned by user");
        }

        updatePost.ifPresent(selectPost-> {
            selectPost.setTitle(dto.getTitle());
            selectPost.setBody(dto.getBody());
            selectPost.setSubCategory(dto.getSubCategory());
            selectPost.setPostStatus(dto.getPostStatus());
            selectPost.setThumbnailUrl(dto.getThumbnailUrl());
            selectPost.setInnerArea(dto.getInnerArea());
            selectPost.setTotalArea(dto.getTotalArea());
            selectPost.setState(dto.getState());
            selectPost.setPrice(dto.getPrice());
            selectPost.setPostStatus(dto.getPostStatus());
            selectPost.setProductType(dto.getProductType());
            selectPost.setLastModified(LocalDateTime.now());

            realEstateRepository.save(selectPost);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetailedRealEstatePost> searchPostsByKeyword(@NotBlank String keyword, int pageNo, String criteria) {

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<RealEstatePost> posts =  realEstateRepository.searchByKeyword(keyword, pageable).getContent();

        List<String> userNames = new ArrayList<>();
        List<Long> likes = new ArrayList<>();
        for (RealEstatePost post : posts) {
            Optional<User> user = userRepository.findById(post.getUserId());
            long like = likeRepository.countByPostId(post.getId());
            if(user.isEmpty()) {
                throw new EntityNotFoundException("User with ID " + post.getUserId() + " not found");
            }
            userNames.add(user.get().getName());
            likes.add(like);
        }

        return DetailedRealEstatePost.toList(posts, userNames, likes);
    }

    public void IncreaseViewCount(Long postId){

        Optional<RealEstatePost> updatePost = realEstateRepository.getPostsById(postId);

        updatePost.ifPresent(selectPost -> {
            selectPost.setView(selectPost.getView() + 1);
            realEstateRepository.save(selectPost);
        });
    }
}
