package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.CreatePostRequestDto;
import com.koreandubai.handubi.controller.dto.SimplePost;
import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.SessionKey;
import com.koreandubai.handubi.global.exception.UnauthorizedException;
import com.koreandubai.handubi.repository.PostRepository;
import com.koreandubai.handubi.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.koreandubai.handubi.global.common.PageSize.NOMAL_PAGE_SIZE;


@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public List<SimplePost> getPosts(CategoryType category, int pageNo, String criteria){

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<Post> posts = postRepository.findAllByCategory(category, pageable).getContent();

        List<String> userNames = new ArrayList<>();
        for (Post post : posts) {
            Optional<User> user = userRepository.findById(post.getUserId());
            if(user.isEmpty()) {
                throw new NoSuchElementException("User with ID " + post.getUserId() + " not found");
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
            throw new NoSuchElementException("Post with ID " + postId + " not found");
        }

        if(!userId.equals(postRepository.getPostsById(postId).get().getUserId())){
            throw new NoSuchElementException("Post with ID " + postId + " is not owned by user");
        }
        postRepository.deleteById(postId);
    }
}