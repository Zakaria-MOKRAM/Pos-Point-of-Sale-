package ma.jway.rms.services;

import ma.jway.rms.dto.models.*;
import ma.jway.rms.dto.responses.AgentResponse;
import ma.jway.rms.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AgentService {
    private final AgentRepository agentRepository;

    public AgentService(
            AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Transactional
    public List<AgentResponse> getAgents() {
        List<AgentResponse> agentResponses = new ArrayList<>();

        List<Agent> agents = agentRepository.findAll();
        for (Agent agent : agents) {
            agentResponses.add(new AgentResponse(
                    agent.getId(),
                    agent.getName(),
                    agent.getTvaRate()));
        }

        return agentResponses;
    }
}
