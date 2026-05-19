package com.edu.a2a.summarizer.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;

/**
 * 요약 서비스
 *
 * ★ 학습 포인트:
 *   - TranslatorService.java 와 동일한 패턴
 *   - 프롬프트 작성 → model.chat() 호출 → 결과 반환
 */
@Service
public class SummarizerService {

    private final ChatLanguageModel model;

    public SummarizerService(ChatLanguageModel model) {
        this.model = model;
    }

    /**
     * 영문 텍스트를 2-3 문장으로 요약합니다.
     */
    public String summarize(String englishText) {
        String prompt = """
            다음 텍스트를 2-3 문장으로 요약하세요 (최대 100 단어).
            요약문만 출력하고 설명이나 부가 문구는 덧붙이지 마세요.

            요약할 텍스트:
            """ + englishText;

        return model.chat(prompt);
    }
}
