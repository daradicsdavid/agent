package application.agent.model;

import application.file.AgentFileData;
import application.util.RandomUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agent {

    private final int number;
    private final Agency agency;
    private final List<String> aliases;
    private final Secrets secrets;
    private boolean arrested = false;

    private final Map<String, Agency> knownAgents;

    public Agent(AgentFileData agentFileData, Agency agency, int number) {
        this.number = number;
        this.agency = agency;
        this.aliases = agentFileData.getAliases();
        secrets = new Secrets();
        secrets.addSecret(agentFileData.getSecret());
        knownAgents = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public String toString() {
        return "Agent{" +
                "number=" + number +
                ", agency=" + agency +
                ", aliases=" + aliases +
                ", secrets=" + secrets +
                '}';
    }

    public boolean isNotArrested() {
        return !arrested;
    }

    public int getNumber() {
        return number;
    }

    public Agency getAgency() {
        return agency;
    }

    public String getRandomAlias() {
        int random = RandomUtils.getRandom(0, aliases.size());
        return aliases.get(random);
    }

    public String getName() {
        return "Agent " + agency + "-" + number;
    }

    public Agency getAgencyOfAgent(String alias) {
        return knownAgents.get(alias);
    }

    public void putAgentToKnownAgents(String alias, Agency agency) {
        knownAgents.put(alias, agency);
    }
}
