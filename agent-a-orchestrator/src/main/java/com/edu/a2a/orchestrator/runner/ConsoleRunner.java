package com.edu.a2a.orchestrator.runner;

import com.edu.a2a.orchestrator.service.PipelineService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleRunner implements CommandLineRunner {

    private final PipelineService pipelineService;

    public ConsoleRunner(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @Override
    public void run(String... args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  A2A 오케스트레이터 에이전트 준비 완료            ║");
        System.out.println("║  GPT-5 Nano  |  동적 라우팅                      ║");
        System.out.println("║  Agent B (요약:8081) + Agent C (번역:8082)        ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("── 샘플 질의 (복사해서 붙여넣기) ────────────────────────────");
        System.out.println();
        System.out.println("  1) 요약만:");
        System.out.println("  다음 글을 요약해줘: Google announced the A2A protocol in April 2025 as an open standard for agent-to-agent communication. The protocol was donated to the Linux Foundation and has since gained support from over 50 technology companies including Salesforce, SAP, and Atlassian. A2A enables AI agents built with different frameworks and programming languages to discover each other and collaborate on complex tasks through standardized HTTP and JSON-RPC interfaces.");
        System.out.println();
        System.out.println("  2) 번역만:");
        System.out.println("  다음 글을 한국어로 번역해줘: OpenAI released GPT-5 in August 2026, featuring significant improvements in reasoning, multimodal understanding, and tool use capabilities. The model introduces a new Mixture-of-Experts architecture that reduces inference costs while maintaining high performance. Enterprise adoption has been rapid, with major banks, healthcare providers, and manufacturing companies integrating GPT-5 into their workflows.");
        System.out.println();
        System.out.println("  3) 요약+번역:");
        System.out.println("  다음 글을 요약하고 한국어로 번역해줘: The rise of multi-agent systems in 2026 has transformed enterprise software development. Companies are moving away from monolithic AI applications toward specialized agents that collaborate through standardized protocols like A2A. This shift mirrors the microservices revolution of the 2010s, where large applications were broken into smaller, independently deployable services. Industry analysts predict that by 2028, over 60 percent of enterprise AI deployments will use multi-agent architectures.");
        System.out.println("────────────────────────────────────────────────────────────");
        System.out.println("종료: quit");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("📝 입력: ");
            String input = scanner.nextLine().trim();
            if (input.isBlank()) continue;
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) break;

            try {
                System.out.println("\n⏳ 처리 중...\n");
                long start = System.currentTimeMillis();
                String result = pipelineService.process(input);
                System.out.println("──────────────────────────────────────────");
                System.out.println("🤖 결과:\n" + result);
                System.out.printf("   (%,dms)%n%n", System.currentTimeMillis() - start);
            } catch (Exception e) {
                System.out.println("❌ 오류: " + e.getMessage() + "\n");
            }
        }

        scanner.close();
        System.out.println("종료되었습니다.");
        System.exit(0);
    }
}
