package com.edu.a2a.orchestrator.agent;

import com.edu.a2a.orchestrator.service.PipelineService;
import io.github.a2ap.core.model.*;
import io.github.a2ap.core.server.AgentExecutor;
import io.github.a2ap.core.server.EventQueue;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * A2A Agent Executor — 오케스트레이터
 *
 * 외부에서 A2A 프로토콜로 요청이 들어오면 PipelineService를 통해 처리합니다.
 * 콘솔(ConsoleRunner)과 A2A 요청 모두 동일한 PipelineService를 사용합니다.
 */
@Component
public class OrchestratorAgentExecutor implements AgentExecutor {

    private final PipelineService pipelineService;

    public OrchestratorAgentExecutor(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @Override
    public Mono<Void> execute(RequestContext context, EventQueue eventQueue) {
        return Mono.fromRunnable(() -> {
            try {
                String inputText = extractText(context);
                String result = pipelineService.process(inputText);

                Message response = Message.builder()
                        .role("agent")
                        .parts(List.of(TextPart.builder().text(result).build()))
                        .build();
                eventQueue.enqueueEvent(response);
                eventQueue.close();
            } catch (Exception e) {
                throw new RuntimeException("파이프라인 처리 실패: " + e.getMessage(), e);
            }
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
