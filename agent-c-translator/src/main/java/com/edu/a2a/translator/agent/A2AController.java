package com.edu.a2a.translator.agent;

import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.server.Dispatcher;
import org.springframework.web.bind.annotation.*;

/**
 * A2A 프로토콜 엔드포인트 (a2a4j SDK 연동)
 *
 * - GET  /.well-known/agent.json  → Agent Card 반환
 * - POST /                        → JSON-RPC 요청 처리 (tasks/send 등)
 */
@RestController
public class A2AController {

    private final AgentCard agentCard;
    private final Dispatcher dispatcher;

    public A2AController(AgentCard agentCard, Dispatcher dispatcher) {
        this.agentCard = agentCard;
        this.dispatcher = dispatcher;
    }

    @GetMapping("/.well-known/agent.json")
    public AgentCard agentCard() {
        return agentCard;
    }

    @PostMapping("/")
    public JSONRPCResponse handleRpc(@RequestBody JSONRPCRequest request) {
        return dispatcher.dispatch(request);
    }
}
