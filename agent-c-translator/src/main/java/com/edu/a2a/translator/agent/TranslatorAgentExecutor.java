package com.edu.a2a.translator.agent;

import com.edu.a2a.translator.service.TranslatorService;
import io.github.a2ap.core.model.*;
import io.github.a2ap.core.server.AgentExecutor;
import io.github.a2ap.core.server.EventQueue;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * A2A Agent Executor — 번역 에이전트
 *
 * a2a4j SDK의 AgentExecutor 인터페이스를 구현합니다.
 * A2A 요청이 들어오면 execute()가 호출되고,
 * 결과를 EventQueue에 넣어 응답합니다.
 */
@Component
public class TranslatorAgentExecutor implements AgentExecutor {

    private final TranslatorService translatorService;

    public TranslatorAgentExecutor(TranslatorService translatorService) {
        this.translatorService = translatorService;
    }

    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        return Mono.fromRunnable(() -> {
            // 1. 요청에서 텍스트 추출
            String inputText = extractText(context);

            // 2. LLM 호출 (번역)
            String translation = translatorService.translate(inputText);

            // 3. 결과를 A2A Artifact로 변환하여 응답
            Message response = Message.builder()
                    .role("agent")
                    .parts(List.of(TextPart.builder().text(translation).build()))
                    .build();
            eventQueue.enqueueEvent(response);
            eventQueue.close();
        });
    }

    @Override
    public Mono<Void> cancel(String taskId) {
        return Mono.empty();
    }

    private String extractText(RequestContext context) {
        Message msg = context.getRequest().getMessage();
        if (msg != null && msg.getParts() != null) {
            for (Part part : msg.getParts()) {
                if (part instanceof TextPart textPart) {
                    return textPart.getText();
                }
            }
        }
        return "";
    }
}
