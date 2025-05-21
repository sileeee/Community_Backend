package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.AdInfo;
import com.koreandubai.handubi.domain.Advertisement;
import com.koreandubai.handubi.global.common.AdStatus;
import com.koreandubai.handubi.global.common.CategoryType;
import com.koreandubai.handubi.repository.AdRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;

    public List<AdInfo> getActiveAdBanners(CategoryType categoryType) {

        List<Advertisement> advertisements = adRepository.findAllByCategoryAndStatus(categoryType, AdStatus.ACTIVE);

        if (advertisements.isEmpty()) {
            throw new EntityNotFoundException("No active advertisements found for the given category.");
        }

        ArrayList<AdInfo> activeAds = new ArrayList<>();
        for(Advertisement advertisement : advertisements) {
            activeAds.add(AdInfo.builder()
                .category(advertisement.getCategory())
                .title(advertisement.getTitle())
                .imageUrl(advertisement.getImageUrl())
                .linkUrl(advertisement.getLinkUrl())
                .build());
        }
        return activeAds;
    }
}


