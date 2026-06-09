package com.kiranvellanki.flowguard.flowguard_core.repository;

import com.kiranvellanki.flowguard.flowguard_core.entity.RateLimitRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RuleRepository extends JpaRepository<RateLimitRuleEntity, String> {
    Optional<RateLimitRuleEntity> findByClientId(String clientId);
    List<RateLimitRuleEntity> findAll();
    boolean existsByClientId(String clientId);
}
