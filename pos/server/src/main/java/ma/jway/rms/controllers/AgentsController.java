package ma.jway.rms.controllers;

import ma.jway.rms.dto.responses.AgentResponse;
import ma.jway.rms.services.AgentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/agents")
public class AgentsController {
    private final AgentService agentService;

    public AgentsController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("")
    public List<AgentResponse> findAgents() {
        return agentService.getAgents();
    }
}
