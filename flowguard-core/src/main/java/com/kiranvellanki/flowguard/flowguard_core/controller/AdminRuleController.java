package com.kiranvellanki.flowguard.flowguard_core.controller;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import com.kiranvellanki.flowguard.flowguard_core.service.AdminRuleService;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

@RestController
@RequestMapping("/admin/rules")
public class AdminRuleController {

	@Autowired
	private AdminRuleService adminRuleService;

	@PostMapping
	public ResponseEntity<RateLimitRule> create(@RequestBody RateLimitRule rule) {
		try {
			RateLimitRule createdRule = adminRuleService.createRule(rule);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdRule);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<List<RateLimitRule>> getAll() {
		try {
			List<RateLimitRule> rules = adminRuleService.getAllRules();
			return ResponseEntity.ok(rules);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("/{clientId}")
	public ResponseEntity<RateLimitRule> get(@PathVariable String clientId) {
		try {
			RateLimitRule rule = adminRuleService.getRuleByClientId(clientId);
			return ResponseEntity.ok(rule);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@PutMapping("/{clientId}")
	public ResponseEntity<RateLimitRule> update(@PathVariable String clientId, @RequestBody RateLimitRule rule) {
		try {
			RateLimitRule updatedRule = new RateLimitRule(clientId, rule.algorithm(), rule.maxRequests(),
					rule.windowSeconds());
			RateLimitRule result = adminRuleService.updateRule(updatedRule);
			return ResponseEntity.ok(result);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@DeleteMapping("/{clientId}")
	public ResponseEntity<Void> delete(@PathVariable String clientId) {
		try {
			adminRuleService.deleteRule(clientId);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
}
