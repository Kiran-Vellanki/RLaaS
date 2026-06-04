package com.kiranvellanki.flowguard.flowguard_core.resolver;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import com.kiranvellanki.flowguard.flowguard_core.repository.RuleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RepositoryRuleResolver implements RuleResolver {

	private final RuleRepository ruleRepository;

	public RepositoryRuleResolver(RuleRepository ruleRepository) {
		this.ruleRepository = ruleRepository;
	}

	@Override
	public Optional<RateLimitRule> getRule(String clientId) {
		return ruleRepository.findByClientId(clientId);
	}
}
