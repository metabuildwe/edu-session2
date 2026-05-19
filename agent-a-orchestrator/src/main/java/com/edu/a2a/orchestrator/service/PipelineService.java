package com.edu.a2a.orchestrator.service;

import io.github.a2ap.core.client.A2AClient;
import io.github.a2ap.core.client.impl.DefaultA2AClient;
import io.github.a2ap.core.client.impl.HttpCardResolver;
import io.github.a2ap.core.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A2A 파이프라인 오케스트레이터
 *
 * a2a4j SDK의 A2AClient를 사용하여 에이전트를 호출합니다.
 * RouterService가 LLM으로 의도를 분류하면,
 * 그 결과(Route)에 따라 적절한 에이전트를 호출합니다.
 *
 * ──────────────────────────────────────────────────────
 * ★ 학습 포인트:
 *   - A2AClient로 Agent Card 조회 → sendMessage() 호출
 *   - switch 분기로 Route에 따라 다른 에이전트 조합을 호출
 *   - 에이전트 추가 시 enum + switch 모두 수정 필요 (확장성 한계)
 * ──────────────────────────────────────────────────────
 */
@Service
public class PipelineService {

    private final RouterService routerService;
    private final A2AClient agentBClient;
    private final A2AClient agentCClient;

    public PipelineService(RouterService routerService) {
        this.routerService = routerService;
        // ★ SDK의 HttpCardResolver로 Agent Card를 자동 발견
        this.agentBClient = new DefaultA2AClient(new HttpCardResolver("http://localhost:8081"));
        this.agentCClient = new DefaultA2AClient(new HttpCardResolver("http://localhost:8082"));
    }

    /**
     * 사용자 입력을 분석하고, 적절한 에이전트를 호출하여 결과를 반환합니다.
     */
    public String process(String userInput) throws Exception {
        // ★ Step 0. LLM으로 의도 분류
        RouterService.Route route = routerService.classify(userInput);

        // ★ Step 1. 분류 결과에 따라 에이전트 호출
        switch (route) {
            case SUMMARIZE: {
                String summary = sendAndExtract(agentBClient, userInput);
                return "[ 요약 ]\n" + summary;
            }
            case TRANSLATE: {
                String translation = sendAndExtract(agentCClient, userInput);
                return "[ 번역 ]\n" + translation;
            }
            case SUMMARIZE_AND_TRANSLATE: {
                // ★ 핵심: B의 결과(summary)를 C의 입력으로 연결
                String summary     = sendAndExtract(agentBClient, userInput);
                String translation = sendAndExtract(agentCClient, summary);
                return "[ 요약 ]\n" + summary +
                       "\n\n[ 번역 ]\n" + translation;
            }
            default:
                return "알 수 없는 라우팅: " + route;
        }
    }

    /**
     * A2A SDK 클라이언트로 에이전트에 메시지를 전송하고 텍스트 결과를 추출합니다.
     */
    private String sendAndExtract(A2AClient client, String text) throws Exception {
        // SDK의 MessageSendParams 빌더로 요청 생성
        MessageSendParams params = MessageSendParams.builder()
                .message(Message.builder()
                        .role("user")
                        .parts(List.of(TextPart.builder().text(text).build()))
                        .build())
                .build();

        // A2A 표준 sendMessage 호출
        SendMessageResponse response = client.sendMessage(params);

        // 응답에서 텍스트 추출
        if (response instanceof Task task) {
            return extractTextFromTask(task);
        } else if (response instanceof Message message) {
            return extractTextFromMessage(message);
        }

        return "";
    }

    private String extractTextFromTask(Task task) {
        if (task.getArtifacts() != null) {
            for (Artifact artifact : task.getArtifacts()) {
                if (artifact.getParts() != null) {
                    for (Part part : artifact.getParts()) {
                        if (part instanceof TextPart tp) return tp.getText();
                    }
                }
            }
        }
        // history에서 agent 메시지 찾기
        if (task.getHistory() != null) {
            for (Message msg : task.getHistory()) {
                if ("agent".equals(msg.getRole())) {
                    return extractTextFromMessage(msg);
                }
            }
        }
        return "";
    }

    private String extractTextFromMessage(Message message) {
        if (message.getParts() != null) {
            for (Part part : message.getParts()) {
                if (part instanceof TextPart tp) return tp.getText();
            }
        }
        return "";
    }
}
