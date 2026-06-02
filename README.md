# FlowGuard - Distributed Rate Limiter as a Service

[![Build](https://github.com/Kiran-Vellanki/RLaaS/actions/workflows/build.yaml/badge.svg)](https://github.com/Kiran-Vellanki/RLaaS/actions/workflows/build.yaml)

Spring Boot based distributed rate limiting platform supporting multiple algorithms and Redis-backed enforcement.


**Expected flow**:
 Client -> Gateway -> Global Filter -> Rule Resolver -> Redis -> DB Fallback -> Allow/ Deny -> Backend Servicre

*In Progress...*
 
