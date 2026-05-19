# 세션 2 실습 — A2A 멀티 에이전트 파이프라인

```
사용자 요청
    │
    ▼
Agent A: 오케스트레이터 (port 8080)  ← TODO 2: process() 구현
    │
    ├──[A2A]──▶ Agent B: 요약 (port 8081)   ← TODO 1: summarize() 구현
    │                │
    │                └──[결과 반환]
    │
    └──[A2A]──▶ Agent C: 번역 (port 8082)   ← 완성 (참고용)
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

## 실행 방법 (터미널 3개)

```bash
# 터미널 1 — Agent C (완성, 먼저 실행)
cd agent-c-translator
cp ../.env .env
../gradlew bootRun           # Windows: ..\gradlew.bat bootRun

# 터미널 2 — Agent B (TODO 1 구현 후)
cd agent-b-summarizer
cp ../.env .env
../gradlew bootRun

# 터미널 3 — Agent A (TODO 2 구현 후)
cd agent-a-orchestrator
cp ../.env .env
../gradlew bootRun
```

---

## 실습 과제

### TODO 1 — SummarizerService.summarize() (⭐ 기본)
`agent-b-summarizer/.../service/SummarizerService.java`

Agent C의 TranslatorService.java 를 참고해서:
1. 요약 지시 프롬프트 작성
2. `model.chat(prompt)` 호출
3. 결과 반환

완성 후 동작 확인:
```bash
curl -X POST http://localhost:8081/tasks/send \
  -H "Content-Type: application/json" \
  -d '{"id":"test-1","message":{"role":"user","parts":[{"type":"text","text":"AI is transforming industries worldwide. Companies are using machine learning to automate tasks and improve efficiency. The technology continues to advance rapidly."}]}}'
```

### TODO 2 — PipelineService.process() (⭐⭐ 핵심)
`agent-a-orchestrator/.../service/PipelineService.java`

RouterService가 분류한 의도(Route)에 따라 에이전트를 동적 호출:
- `SUMMARIZE` → Agent B만 호출
- `TRANSLATE` → Agent C만 호출
- `SUMMARIZE_AND_TRANSLATE` → Agent B → Agent C 순서 호출

switch 분기 + callAgent() 호출 + 결과 조합

완성 후 전체 파이프라인 테스트:
```bash
curl -X POST http://localhost:8080/tasks/send \
  -H "Content-Type: application/json" \
  -d '{"id":"pipeline-1","message":{"role":"user","parts":[{"type":"text","text":"[영문 기사 붙여넣기]"}]}}'
```

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
1. `agent-d-sentiment` 모듈 생성 (Agent C 를 복사해서 수정)
2. PipelineService 에 Agent D 호출 추가
3. 최종 결과에 감성 분석 결과 포함
