package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.Home;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HomeRepository extends JpaRepository<Home, Long> {

    void deleteByPostId(Long postId);
}
