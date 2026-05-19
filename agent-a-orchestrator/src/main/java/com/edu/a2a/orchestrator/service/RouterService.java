package com.edu.a2a.orchestrator.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;

/**
 * 사용자 의도를 분류하여 어떤 에이전트를 호출할지 결정합니다.
 *
 * LLM에게 의도 분류를 요청하고, 결과에 따라 라우팅 경로를 반환합니다.
 *   - SUMMARIZE        → Agent B만 호출
 *   - TRANSLATE         → Agent C만 호출
 *   - SUMMARIZE_AND_TRANSLATE → Agent B → Agent C 순서 호출
 */
@Service
public class RouterService {

    private final ChatLanguageModel model;

    public RouterService(ChatLanguageModel model) {
        this.model = model;
    }

    public enum Route {
        SUMMARIZE,
        TRANSLATE,
        SUMMARIZE_AND_TRANSLATE
    }

    /**
     * 사용자 입력을 분석하여 라우팅 경로를 결정합니다.
     */
    public Route classify(String userInput) {
        String prompt = """
            당신은 텍스트 처리 파이프라인의 의도 분류기입니다.
            사용자의 요청을 아래 3가지 중 정확히 하나로 분류하세요.

            - SUMMARIZE : 텍스트 요약만 원함
            - TRANSLATE : 한국어 번역만 원함
            - SUMMARIZE_AND_TRANSLATE : 요약과 번역 모두 원함

            카테고리 이름만 한 단어로 답하세요. 설명은 절대 덧붙이지 마세요.

            사용자 요청:
            """ + userInput;

        String result = model.chat(prompt).trim().toUpperCase();

        if (result.contains("SUMMARIZE_AND_TRANSLATE")) {
            return Route.SUMMARIZE_AND_TRANSLATE;
        } else if (result.contains("TRANSLATE")) {
            return Route.TRANSLATE;
        } else if (result.contains("SUMMARIZE")) {
            return Route.SUMMARIZE;
        }

        // 기본값: 둘 다 수행
        return Route.SUMMARIZE_AND_TRANSLATE;
    }
}
