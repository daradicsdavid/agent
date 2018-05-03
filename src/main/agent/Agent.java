package main.agent;

import java.util.List;

public class Agent {

    private final int number;
    private final Agency agency;
    private final List aliases;

    public Agent(Integer number, Agency agency, List aliases) {
        this.number = number;
        this.agency = agency;
        this.aliases = aliases;
    }


}
