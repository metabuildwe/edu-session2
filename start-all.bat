@echo off
chcp 65001 >nul 2>&1
title A2A Multi-Agent Pipeline

echo ╔══════════════════════════════════════════╗
echo ║  A2A 멀티 에이전트 파이프라인 시작        ║
echo ╚══════════════════════════════════════════╝
echo.

REM .env 파일 복사 (없으면)
if not exist "agent-c-translator\.env" (
    copy .env.example agent-c-translator\.env >nul
    echo 📋 agent-c-translator\.env 생성됨
)
if not exist "agent-b-summarizer\.env" (
    copy .env.example agent-b-summarizer\.env >nul
    echo 📋 agent-b-summarizer\.env 생성됨
)
if not exist "agent-a-orchestrator\.env" (
    copy .env.example agent-a-orchestrator\.env >nul
    echo 📋 agent-a-orchestrator\.env 생성됨
)

echo.
echo 🚀 Agent C (번역, port 8082) 시작 중...
start "Agent-C" /min cmd /c "cd agent-c-translator && ..\gradlew.bat bootRun --quiet > %TEMP%\a2a-agent-c.log 2>&1"
timeout /t 10 /nobreak >nul

echo 🚀 Agent B (요약, port 8081) 시작 중...
start "Agent-B" /min cmd /c "cd agent-b-summarizer && ..\gradlew.bat bootRun --quiet > %TEMP%\a2a-agent-b.log 2>&1"
timeout /t 10 /nobreak >nul

echo ✅ Agent B, C 시작 완료
echo.
echo 🚀 Agent A (오케스트레이터, port 8080) 시작 중...
echo.
cd agent-a-orchestrator
..\gradlew.bat bootRun --quiet

echo.
echo 🛑 에이전트 종료 중...
taskkill /fi "WINDOWTITLE eq Agent-B" /f >nul 2>&1
taskkill /fi "WINDOWTITLE eq Agent-C" /f >nul 2>&1
echo ✅ 모든 에이전트가 종료되었습니다.
pause
