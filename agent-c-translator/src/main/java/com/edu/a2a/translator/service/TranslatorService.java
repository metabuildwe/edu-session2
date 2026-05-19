package com.edu.a2a.translator.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;

/**
 * 번역 서비스 — 완성된 예시
 *
 * ★ Agent B 의 SummarizerService.java 를 구현할 때 이 파일을 참고하세요.
 *
 * 핵심 패턴:
 *   ChatLanguageModel.chat(prompt) → LLM 호출 → 결과 문자열 반환
 */
@Service
public class TranslatorService {

    private final ChatLanguageModel model;

    public TranslatorService(ChatLanguageModel model) {
        this.model = model;
    }

    /**
     * 영문 텍스트를 한국어로 번역합니다.
     */
    public String translate(String englishText) {
        String prompt = """
            다음 영문 텍스트를 자연스러운 한국어로 번역하세요.
            번역문만 출력하세요. 설명, 전제, 부가 설명은 절대 덧붙이지 마세요.
            최대 150단어 이내로 번역하세요.

            번역할 텍스트:
            """ + englishText;

        return model.chat(prompt);
    }
}
