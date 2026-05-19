#!/bin/bash
# A2A 멀티 에이전트 일괄 실행 스크립트
# 사용법: ./start-all.sh

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
GRADLEW="$SCRIPT_DIR/gradlew"

echo "╔══════════════════════════════════════════╗"
echo "║  A2A 멀티 에이전트 파이프라인 시작        ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# .env 파일 확인
if [ ! -f "$SCRIPT_DIR/agent-c-translator/.env" ] && [ ! -f "$SCRIPT_DIR/.env.example" ]; then
    echo "❌ .env.example 파일이 없습니다."
    exit 1
fi

# .env 파일 복사 (없으면)
for dir in agent-c-translator agent-b-summarizer agent-a-orchestrator; do
    if [ ! -f "$SCRIPT_DIR/$dir/.env" ]; then
        cp "$SCRIPT_DIR/.env.example" "$SCRIPT_DIR/$dir/.env"
        echo "📋 $dir/.env 생성됨 (.env.example 복사)"
    fi
done

# Agent C (번역) 실행
echo ""
echo "🚀 Agent C (번역, port 8082) 시작 중..."
cd "$SCRIPT_DIR/agent-c-translator" && "$GRADLEW" bootRun --quiet > /tmp/a2a-agent-c.log 2>&1 &
PID_C=$!
sleep 8

if curl -s http://localhost:8082/.well-known/agent.json > /dev/null 2>&1; then
    echo "✅ Agent C 시작 완료 (PID: $PID_C)"
else
    echo "❌ Agent C 시작 실패. 로그: /tmp/a2a-agent-c.log"
    exit 1
fi

# Agent B (요약) 실행
echo "🚀 Agent B (요약, port 8081) 시작 중..."
cd "$SCRIPT_DIR/agent-b-summarizer" && "$GRADLEW" bootRun --quiet > /tmp/a2a-agent-b.log 2>&1 &
PID_B=$!
sleep 8

if curl -s http://localhost:8081/.well-known/agent.json > /dev/null 2>&1; then
    echo "✅ Agent B 시작 완료 (PID: $PID_B)"
else
    echo "❌ Agent B 시작 실패. 로그: /tmp/a2a-agent-b.log"
    kill $PID_C 2>/dev/null
    exit 1
fi

# Agent A (오케스트레이터) 실행 — 콘솔 대화 모드
echo "🚀 Agent A (오케스트레이터, port 8080) 시작 중..."
echo ""
cd "$SCRIPT_DIR/agent-a-orchestrator" && "$GRADLEW" bootRun --quiet 2>&1

# Agent A 종료 시 B, C도 정리
echo ""
echo "🛑 에이전트 종료 중..."
kill $PID_B $PID_C 2>/dev/null
wait $PID_B $PID_C 2>/dev/null
echo "✅ 모든 에이전트가 종료되었습니다."
