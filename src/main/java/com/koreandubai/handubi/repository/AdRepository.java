package com.koreandubai.handubi.repository;


import com.koreandubai.handubi.domain.Advertisement;
import com.koreandubai.handubi.global.common.AdStatus;
import com.koreandubai.handubi.global.common.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Advertisement, Long> {

    List<Advertisement> findAllByCategoryAndStatus(CategoryType category, AdStatus status);

}
