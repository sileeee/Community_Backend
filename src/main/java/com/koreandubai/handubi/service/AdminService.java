package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.RewardMemberDto;
import com.koreandubai.handubi.controller.dto.UserPointSummaryDto;
import com.koreandubai.handubi.domain.Product;
import com.koreandubai.handubi.domain.RewardRequest;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.domain.UserPoint;
import com.koreandubai.handubi.repository.ProductRepository;
import com.koreandubai.handubi.repository.RewardRequestRepository;
import com.koreandubai.handubi.repository.UserPointRepository;
import com.koreandubai.handubi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserPointRepository userPointRepository;
    private final RewardRequestRepository rewardRequestRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public Map<String, Object> pointList(String q) {
        var list = userPointRepository.searchWithUser(
                (q == null || q.isBlank()) ? null : q);
        return Map.of(
                "members", list,
                "totalMembers", list.size(),
                "totalPoints", list.stream()
                        .mapToInt(UserPointSummaryDto::getTotalPoints)
                        .sum()
        );
    }

    public List<RewardMemberDto> getRewardRequestList(String query) {
        List<RewardRequest> requests = rewardRequestRepository.findAllOrderByCreatedAtDesc();

        return requests.stream()
                .map(r -> {
                    User user = userRepository.findById(r.getUserId())
                            .orElseThrow(() -> new RuntimeException("User not found: " + r.getUserId()));

                    int totalPts = userPointRepository.findByUserId(user.getId())
                            .map(UserPoint::getTotalPoints)
                            .orElse(0);

                    return RewardMemberDto.builder()
                            .id(r.getId())
                            .userId(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .totalPoints(totalPts)
                            .productId(r.getProductId())
                            .pointsUsed(r.getPointsUsed())
                            .status(r.getStatus().name())
                            .createdAt(r.getCreatedAt())
                            .updatedAt(r.getUpdatedAt())
                            .build();
                })
                .filter(dto -> query == null || query.isBlank()
                        || dto.getName().contains(query)
                        || dto.getEmail().contains(query))
                .toList();
    }

    @Transactional
    public void rewardAccept(Long requestId) {

        RewardRequest req = rewardRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("RewardRequest not found: " + requestId));

        if (req.getStatus() != RewardRequest.RequestStatus.PENDING)
            throw new IllegalStateException("Already processed");

        req.setStatus(RewardRequest.RequestStatus.APPROVED);
        req.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void rewardDecline(Long requestId) {

        RewardRequest req = rewardRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("RewardRequest not found: " + requestId));

        if (req.getStatus() != RewardRequest.RequestStatus.PENDING)
            throw new IllegalStateException("Already processed");

        UserPoint up = userPointRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new IllegalStateException("UserPoint not found"));

        up.setTotalPoints(up.getTotalPoints() + req.getPointsUsed());
        up.setUpdatedAt(LocalDateTime.now());

        req.setStatus(RewardRequest.RequestStatus.REJECTED);
        req.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void registerEvent(String name, String description, int pointPrice, int stockQty, LocalDateTime deadline) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .pointPrice(pointPrice)
                .stockQty(stockQty)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .deadline(deadline)
                .build();

        productRepository.save(product);
    }

    @Transactional
    public void deactivateEvent(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public void activateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setActive(true);
        productRepository.save(product);
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}