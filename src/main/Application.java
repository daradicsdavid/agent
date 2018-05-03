package main;

import main.agent.Agency;
import main.agent.AgentBuilder;
import main.util.NumberUtils;

public class Application {

    private int numberOfFirstAgencyMembers;
    private int numberOfSecondAgencyMembers;
    private int lowerBoundaryOfWait;
    private int upperBoundaryOfWait;

    public static void main(String[] args) {
        try {
            Application application = new Application(args);
            application.start();
        } catch (Exception e) {
            OutputWriter.print("Hiba történt a futtatás során!");
            System.exit(1);
        }
    }

    public Application(String[] args) {
        checkArgs(args);
        AgentBuilder agentBuilder = new AgentBuilder(numberOfFirstAgencyMembers, numberOfSecondAgencyMembers);
        agentBuilder.build();
    }

    private void start() {

    }

    private void checkArgs(String[] args) {
        if (args.length != 4) {
            OutputWriter.print("Nem megfelelő a bemenetek száma!");
            throw new IllegalArgumentException();
        }
        numberOfFirstAgencyMembers = NumberUtils.toNumber(args[0]);
        numberOfSecondAgencyMembers = NumberUtils.toNumber(args[1]);
        lowerBoundaryOfWait = NumberUtils.toNumber(args[2]);
        upperBoundaryOfWait = NumberUtils.toNumber(args[3]);
    }
}
