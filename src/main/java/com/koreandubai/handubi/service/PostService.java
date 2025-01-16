package com.koreandubai.handubi.service;

import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.global.common.SubCategoryType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

import java.util.*;


public interface PostService<T, CD, ED> {

    List<T> getPosts(CategoryType category, SubCategoryType subCategory, int pageNo, String criteria);

    void createPost(HttpServletRequest request, CategoryType category, CD dto);

    void deletePost(HttpServletRequest request, Long postId);

    void editPost(HttpServletRequest request, long postId, ED dto);

    void IncreaseViewCount(Long postId);

    long calculateTimeUntilMidnight();

    void PreventDuplicatedView(Long userId, Long postId);

    T getSinglePost(long postId);

    List<T> searchPostsByKeyword(@NotBlank String keyword, int pageNo, String criteria);

}
