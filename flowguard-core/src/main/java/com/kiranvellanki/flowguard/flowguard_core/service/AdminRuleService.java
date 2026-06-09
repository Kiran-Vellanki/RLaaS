package com.kiranvellanki.flowguard.flowguard_core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import com.kiranvellanki.flowguard.flowguard_core.entity.RateLimitRuleEntity;
import com.kiranvellanki.flowguard.flowguard_core.repository.RuleRepository;


@Service
public class AdminRuleService {

    @Autowired
    private RuleRepository ruleRepository;

    
    public RateLimitRule createRule(RateLimitRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Rate limit rule cannot be null");
        }
        if (rule.clientId() == null || rule.clientId().trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }
        if (rule.maxRequests() <= 0) {
            throw new IllegalArgumentException("Max requests must be greater than 0");
        }
        if (rule.windowSeconds() <= 0) {
            throw new IllegalArgumentException("Window seconds must be greater than 0");
        }

        try {
            RateLimitRuleEntity entity = modelToEntity(rule);
            RateLimitRuleEntity savedEntity = ruleRepository.save(entity);
            return entityToModel(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create rate limit rule: " + e.getMessage(), e);
        }
    }


    public RateLimitRule getRuleByClientId(String clientId) {
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }

        try {
            Optional<RateLimitRuleEntity> entity = ruleRepository.findByClientId(clientId);
            if (entity.isEmpty()) {
                throw new RuntimeException("Rate limit rule not found for client: " + clientId);
            }
            return entityToModel(entity.get());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve rate limit rule: " + e.getMessage(), e);
        }
    }

    public List<RateLimitRule> getAllRules() {
        try {
            return ruleRepository.findAll()
                    .stream()
                    .map(this::entityToModel)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve all rate limit rules: " + e.getMessage(), e);
        }
    }

    public RateLimitRule updateRule(RateLimitRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Rate limit rule cannot be null");
        }
        if (rule.clientId() == null || rule.clientId().trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }

        try {
            if (!ruleRepository.existsByClientId(rule.clientId())) {
                throw new RuntimeException("Rate limit rule not found for client: " + rule.clientId());
            }

            RateLimitRuleEntity entity = modelToEntity(rule);
            RateLimitRuleEntity updatedEntity = ruleRepository.save(entity);
            return entityToModel(updatedEntity);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update rate limit rule: " + e.getMessage(), e);
        }
    }

    public boolean deleteRule(String clientId) {
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }

        try {
            if (!ruleRepository.existsByClientId(clientId)) {
                throw new RuntimeException("Rate limit rule not found for client: " + clientId);
            }
            ruleRepository.deleteById(clientId);
            return true;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete rate limit rule: " + e.getMessage(), e);
        }
    }

   
    public boolean ruleExists(String clientId) {
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }

        try {
            return ruleRepository.existsByClientId(clientId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check rule existence: " + e.getMessage(), e);
        }
    }

   
    private RateLimitRuleEntity modelToEntity(RateLimitRule rule) {
        if (rule == null) {
            return null;
        }
        return new RateLimitRuleEntity(
                rule.clientId(),
                rule.algorithm(),
                rule.maxRequests(),
                rule.windowSeconds()
        );
    }

    private RateLimitRule entityToModel(RateLimitRuleEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RateLimitRule(
                entity.getClientId(),
                entity.getAlgorithm(),
                entity.getMaxRequests(),
                entity.getWindowSeconds()
        );
    }
}
