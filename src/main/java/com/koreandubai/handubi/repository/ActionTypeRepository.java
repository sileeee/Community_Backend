package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.ActionType;
import com.koreandubai.handubi.global.common.ActionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActionTypeRepository extends JpaRepository<ActionType, Long> {
    Optional<ActionType> findByActionCode(ActionCode actionCode);
}
