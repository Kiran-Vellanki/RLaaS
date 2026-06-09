package com.kiranvellanki.flowguard.flowguard_core.entity;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitAlgorithmType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rate_limit_rules")
@Data
@NoArgsConstructor
public class RateLimitRuleEntity {

    @Id
    private String clientId;
    private RateLimitAlgorithmType algorithm;
    private int maxRequests;
    private int windowSeconds;

    public RateLimitRuleEntity(String clientId, RateLimitAlgorithmType algorithm, int maxRequests, int windowSeconds) {
        this.clientId = clientId;
        this.algorithm = algorithm;
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }
}
