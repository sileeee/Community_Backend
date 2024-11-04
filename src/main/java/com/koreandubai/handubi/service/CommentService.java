package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.CommentRequestDto;
import com.koreandubai.handubi.controller.dto.EditCommentRequestDto;
import com.koreandubai.handubi.controller.dto.SimpleComment;
import com.koreandubai.handubi.domain.Comment;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.SessionKey;
import com.koreandubai.handubi.global.exception.UnauthorizedException;
import com.koreandubai.handubi.repository.CommentRepository;
import com.koreandubai.handubi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.koreandubai.handubi.global.common.PageSize.NOMAL_PAGE_SIZE;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    public List<SimpleComment> getAllComments(Long postId, int pageNo) {

        Pageable pageable = PageRequest.of(pageNo, NOMAL_PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        List<Comment> comments = commentRepository.findAllByPostId(postId, pageable).getContent();

        List<String> userNames = new ArrayList<>();
        for (Comment comment : comments) {
            Optional<User> user = userRepository.findById(comment.getUserId());
            if(user.isEmpty()) {
                throw new EntityNotFoundException("User with ID " + comment.getUserId() + " not found");
            }
            userNames.add(user.get().getName());
        }
        return SimpleComment.toList(comments, userNames);
    }

    @Transactional
    public void createComment(HttpServletRequest request, CommentRequestDto dto) {

        HttpSession session = request.getSession();
        Long userId = (Long) Optional.ofNullable(session.getAttribute(SessionKey.LOGIN_USER_ID)).orElseThrow(UnauthorizedException::new);

        Comment comment = Comment.builder()
                .postId(dto.getPostId())
                .body(dto.getContent())
                .userId(userId)
                .preCommentId(dto.getPreCommentId())
                .lastModified(LocalDateTime.now())
                .isDeleted(false)
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(HttpServletRequest request, Long commentId, EditCommentRequestDto dto) {

        HttpSession session = request.getSession();
        Long userId = (Long) Optional.ofNullable(session.getAttribute(SessionKey.LOGIN_USER_ID)).orElseThrow(UnauthorizedException::new);

        if(commentRepository.getCommentById(commentId).isEmpty()){
            throw new EntityNotFoundException("Comment with ID " + commentId + " not found");
        }

        Optional<Comment> updateComment = commentRepository.getCommentById(commentId);

        if(!userId.equals(updateComment.get().getUserId())){
            throw new UnauthorizedException("Comment with ID " + commentId + " is not owned by user");
        }

        updateComment.ifPresent(selectComment-> {
            selectComment.setBody(dto.getContent());
            selectComment.setLastModified(LocalDateTime.now());

            commentRepository.save(selectComment);
        });
    }

    @Transactional
    public void deleteComment(HttpServletRequest request, Long commentId) {

        HttpSession session = request.getSession();
        Long userId = (Long) Optional.ofNullable(session.getAttribute(SessionKey.LOGIN_USER_ID)).orElseThrow(UnauthorizedException::new);

        if(commentRepository.getCommentById(commentId).isEmpty()){
            throw new EntityNotFoundException("Comment with ID " + commentId + " not found");
        }

        if(!userId.equals(commentRepository.getCommentById(commentId).get().getUserId())){
            throw new UnauthorizedException("Comment with ID " + commentId + " is not owned by user");
        }
        commentRepository.deleteById(commentId);
    }
}

