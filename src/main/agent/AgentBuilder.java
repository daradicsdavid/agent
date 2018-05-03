package main.agent;

import main.file.AgentFileData;
import main.file.AgentReader;

import static main.agent.Agency.First;
import static main.agent.Agency.Second;

public class AgentBuilder {

    private final int numberOfFirstAgencyMembers;
    private final int numberOfSecondAgencyMembers;
    private final AgentReader agentReader;

    public AgentBuilder(int numberOfFirstAgencyMembers, int numberOfSecondAgencyMembers) {
        this.numberOfFirstAgencyMembers = numberOfFirstAgencyMembers;
        this.numberOfSecondAgencyMembers = numberOfSecondAgencyMembers;

        agentReader = new AgentReader();
    }

    public void build() {
        for (int i = 1; i <= numberOfFirstAgencyMembers; i++) {
            createAgent(First, i);
        }
        for (int i = 1; i <= numberOfSecondAgencyMembers; i++) {
            createAgent(Second, i);
        }
    }

    private void createAgent(Agency agency, int agentNumber) {
        AgentFileData agentFileData = agentReader.readAgentDataFromFile(agency, agentNumber);


    }


}
