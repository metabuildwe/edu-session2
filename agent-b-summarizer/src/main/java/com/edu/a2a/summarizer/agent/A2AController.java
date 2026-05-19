package com.edu.a2a.summarizer.agent;

import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.server.Dispatcher;
import org.springframework.web.bind.annotation.*;

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
