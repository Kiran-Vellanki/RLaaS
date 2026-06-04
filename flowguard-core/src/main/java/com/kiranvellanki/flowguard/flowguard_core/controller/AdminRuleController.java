package com.kiranvellanki.flowguard.flowguard_core.controller;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import com.kiranvellanki.flowguard.flowguard_core.repository.RuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("/admin/rules")
public class AdminRuleController {

	private final RuleRepository ruleRepository;

	public AdminRuleController(RuleRepository ruleRepository) {
		this.ruleRepository = ruleRepository;
	}

	@PostMapping
	public ResponseEntity<RateLimitRule> create(@RequestBody RateLimitRule rule) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ruleRepository.save(rule));
	}

	@GetMapping
	public Collection<RateLimitRule> getAll() {
		return ruleRepository.findAll();
	}

	@GetMapping("/{clientId}")
	public RateLimitRule get(@PathVariable String clientId) {
		return ruleRepository.findByClientId(clientId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PutMapping("/{clientId}")
	public RateLimitRule update(@PathVariable String clientId, @RequestBody RateLimitRule rule) {
		RateLimitRule updatedRule = new RateLimitRule(clientId, rule.algorithm(), rule.limit(), rule.windowSeconds());
		return ruleRepository.save(updatedRule);
	}

	@DeleteMapping("/{clientId}")
	public ResponseEntity<Void> delete(@PathVariable String clientId) {
		if (!ruleRepository.deleteByClientId(clientId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.noContent().build();
	}
}
