package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.CreatePostRequestDto;
import com.koreandubai.handubi.controller.dto.DetailedPost;
import com.koreandubai.handubi.controller.dto.EditPostRequestDto;
import com.koreandubai.handubi.controller.dto.GetUploadedImage;
import com.koreandubai.handubi.domain.Post;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.PostStatus;
import com.koreandubai.handubi.global.common.SubCategoryType;
import com.koreandubai.handubi.global.exception.UnauthorizedException;
import com.koreandubai.handubi.global.util.RedisUtil;
import com.koreandubai.handubi.repository.LikeRepository;
import com.koreandubai.handubi.repository.PostRepository;
import com.koreandubai.handubi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.koreandubai.handubi.global.common.PageSize.NOMAL_PAGE_SIZE;


@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final RedisUtil redisUtil;
    private final UserService userService;

    @Value("${upload.directory}")
    private String uploadDir;

    @Value("${upload.user}")
    private String user;

    public List<DetailedPost> getPosts(CategoryType category, SubCategoryType subCategory, int pageNo, String criteria){

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));

        List<Post> posts;
        if(subCategory.equals(SubCategoryType.TOTAL)){
            posts = postRepository.findAllByCategoryAndStatus(category, PostStatus.PUBLIC, pageable).getContent();
        }else {
            posts = postRepository.findAllByCategoryAndSubCategoryAndStatus(category, subCategory, PostStatus.PUBLIC, pageable).getContent();
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

        long like = likeRepository.countByPostId(post.get().getId());

        PreventDuplicatedView(user.get().getId(), postId);

        return DetailedPost.builder()
                .id(postId)
                .title(post.get().getTitle())
                .body(post.get().getBody())
                .author(user.get().getName())
                .category(post.get().getCategory())
                .subCategory(post.get().getSubCategory())
                .status(post.get().getStatus())
                .createdAt(post.get().getCreatedAt())
                .view(post.get().getView())
                .like(like)
                .build();
    }

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

    public String uploadImage(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new EntityNotFoundException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image file can be uploaded.");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Unable to create upload directory.");
            }
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID() + fileExtension;

        File targetFile = new File(dir, uniqueFileName);

        file.transferTo(targetFile);

        targetFile.setReadable(true, false);

        Path path = targetFile.toPath();
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal owner = lookupService.lookupPrincipalByName(user);
        Files.setOwner(path, owner);

        return "https://handubi.com/api/posts/images/" + uniqueFileName;
    }

    public GetUploadedImage getImage(String imageName){

        Path imagePath = Paths.get(uploadDir).resolve(imageName);

        Resource resource = new FileSystemResource(imagePath);

        if (!resource.exists()) {
            throw new EntityNotFoundException("image not found");
        }

        String fileExtension = imageName.substring(imageName.lastIndexOf(".") + 1).toLowerCase();
        MediaType mediaType = switch (fileExtension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        return GetUploadedImage.builder()
                .contentType(mediaType)
                .body(resource)
                .build();
    }
}