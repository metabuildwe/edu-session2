# 세션 2 실습 — A2A 멀티 에이전트 파이프라인

```
사용자 요청
    │
    ▼
Agent A: 오케스트레이터 (port 8080)  ← LLM 의도 분류 + 동적 라우팅
    │
    ├──[A2A]──▶ Agent B: 요약 (port 8081)
    │                │
    │                └──[결과 반환]
    │
    └──[A2A]──▶ Agent C: 번역 (port 8082)
                         │
                         └──[결과 반환]
```

---

## 시작 전 준비
```bash
cd session2

# 1. API 키 설정 (.env 파일 생성)
cp .env.example .env
# LLM_API_KEY 값 입력

# 2. 빌드
./gradlew build -q          # Windows: gradlew.bat build -q
```

API 키는 교육 시 별도 안내됩니다.

---

## 실행 방법

### 한 번에 실행 (권장)
```bash
./start-all.sh              # Windows: start-all.bat
```

### 개별 실행 (터미널 3개)
```bash
# 터미널 1 — Agent C (번역)
cd agent-c-translator
cp ../.env .env
../gradlew bootRun           # Windows: ..\gradlew.bat bootRun

# 터미널 2 — Agent B (요약)
cd agent-b-summarizer
cp ../.env .env
../gradlew bootRun

# 터미널 3 — Agent A (오케스트레이터)
cd agent-a-orchestrator
cp ../.env .env
../gradlew bootRun
```

---

## 프로젝트 구조

| 모듈 | 역할 | 핵심 클래스 |
|------|------|------------|
| **agent-a-orchestrator** | LLM 의도 분류 + A2A Client로 B/C 호출 | RouterService, PipelineService |
| **agent-b-summarizer** | 영문 텍스트 요약 (A2A Server) | SummarizerAgentExecutor |
| **agent-c-translator** | 한국어 번역 (A2A Server) | TranslatorAgentExecutor |

### SDK 구성
- **a2a4j**: A2A 프로토콜 Spring Boot Starter (Agent Card 선언, AgentExecutor, A2AClient)
- **LangChain4j**: LLM 호출 (GPT-5 Nano, OpenAI 호환 API)

---

## A2A 발견(Discovery) 확인
```bash
curl http://localhost:8080/.well-known/agent.json   # Agent A Card
curl http://localhost:8081/.well-known/agent.json   # Agent B Card
curl http://localhost:8082/.well-known/agent.json   # Agent C Card
```

---

## 도전 과제 (⭐⭐⭐ 심화)
Agent D 추가: 감성 분석 에이전트 (Positive / Neutral / Negative)

수정이 필요한 3곳:
1. `agent-d-sentiment/` 모듈 생성 (Agent C를 복사해서 프롬프트만 변경)
2. `RouterService.java` — Route enum에 SENTIMENT 추가 + classify 프롬프트 수정
3. `PipelineService.java` — switch에 case SENTIMENT 추가 + agentDClient 추가
