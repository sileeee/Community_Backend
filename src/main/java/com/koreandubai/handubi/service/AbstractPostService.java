package com.koreandubai.handubi.service;

import com.koreandubai.handubi.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public abstract class AbstractPostService<T, CD, ED> implements PostService<T, CD, ED>{

    private final RedisUtil redisUtil;


    public long calculateTimeUntilMidnight() {

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
}
