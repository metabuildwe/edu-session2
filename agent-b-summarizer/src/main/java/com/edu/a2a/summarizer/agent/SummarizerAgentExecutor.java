package com.edu.a2a.summarizer.agent;

import com.edu.a2a.summarizer.service.SummarizerService;
import io.github.a2ap.core.model.*;
import io.github.a2ap.core.server.AgentExecutor;
import io.github.a2ap.core.server.EventQueue;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * A2A Agent Executor — 요약 에이전트
 *
 * ★ 학습 포인트:
 *   - TranslatorAgentExecutor와 동일한 패턴
 *   - extractText() → LLM 호출 → EventQueue에 결과 전달
 */
@Component
public class SummarizerAgentExecutor implements AgentExecutor {

    private final SummarizerService summarizerService;

    public SummarizerAgentExecutor(SummarizerService summarizerService) {
        this.summarizerService = summarizerService;
    }

    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        return Mono.fromRunnable(() -> {
            String inputText = extractText(context);
            String summary = summarizerService.summarize(inputText);

            Message response = Message.builder()
                    .role("agent")
                    .parts(List.of(TextPart.builder().text(summary).build()))
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
