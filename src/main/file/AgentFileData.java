package main.file;

import main.agent.Agency;

import java.util.List;

public class AgentFileData {
    private final List aliases;
    private final String secret;

    public AgentFileData(List aliases, String secret) {
        this.aliases = aliases;
        this.secret = secret;
    }

    public List getAliases() {
        return aliases;
    }

    public String getSecret() {
        return secret;
    }
}
