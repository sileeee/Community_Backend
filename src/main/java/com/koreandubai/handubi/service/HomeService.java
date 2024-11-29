package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.EditMainPostDto;
import com.koreandubai.handubi.controller.dto.MainPost;
import com.koreandubai.handubi.domain.Home;
import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.global.util.auth.AuthRequired;
import com.koreandubai.handubi.repository.HomeRepository;
import com.koreandubai.handubi.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeRepository homeRepository;
    private final PostRepository postRepository;

    public List<MainPost> getMainPosts() {

        List<Home> homeList = homeRepository.findAll();

        return homeList.stream()
                .map(home -> {
                    Post post = postRepository.findById(home.getPostId())
                            .orElseThrow(() -> new EntityNotFoundException("Post not found for ID: " + home.getPostId()));
                    return MainPost.builder()
                            .postId(home.getPostId())
                            .locationId(home.getLocationId())
                            .title(post.getTitle())
                            .content(post.getBody())
                            .imageUrl(home.getImageUrl())
                            .categoryType(post.getCategory())
                            .subCategoryType(post.getSubCategory())
                            .build();
                })
                .collect(Collectors.toList());
    }


    @AuthRequired
    public void editMainPosts(EditMainPostDto dto){

        postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Home entry not found for ID: " + dto.getPostId()));

        homeRepository.deleteByPostId(dto.getPostId());

        Home home = Home.builder()
                .postId(dto.getPostId())
                .locationId(dto.getLocationId())
                .imageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : "none")
                .build();
        homeRepository.save(home);
    }
}

