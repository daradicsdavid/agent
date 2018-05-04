package application.agent;

import application.AgentConfiguration;
import application.OutputWriter;
import application.agent.client.AgentClient;
import application.agent.model.Agent;
import application.agent.server.AgentServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgentRunner {

    private final AgentClient agentClient;
    private final AgentServer agentServer;
    private Agent agent;
    private ExecutorService agentExecutor;
    private final OutputWriter outputWriter;

    public AgentRunner(Agent agent, AgentConfiguration agentConfiguration) {
        this.agent = agent;
        agentServer = new AgentServer(agent, agentConfiguration);
        agentClient = new AgentClient(agent, agentConfiguration, agentServer);
        outputWriter = new OutputWriter(agent.getName() + " OutputWriter");
    }


    public void start() {
        outputWriter.print("Agent indul.", agent.getAgency(), String.valueOf(agent.getNumber()));
        agentExecutor = Executors.newFixedThreadPool(2);
        agentExecutor.submit(agentServer);
        agentExecutor.submit(agentClient);
    }
}
