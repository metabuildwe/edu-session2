package com.edu.a2a.translator.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class LlmConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        String apiKey = Dotenv.configure().ignoreIfMissing().load().get("LLM_API_KEY");
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalStateException(
                "[오류] LLM_API_KEY 가 없습니다.\n" +
                "  프로젝트 루트에 .env 파일 생성: LLM_API_KEY=발급받은키"
            );

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://mlapi.run/daef5150-72ef-48ff-8861-df80052ea7ac/v1")
                .modelName("openai/gpt-5-nano")
                .timeout(Duration.ofSeconds(60))
                .build();
    }
}
