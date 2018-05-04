package application.agent.client;

import application.AgentConfiguration;
import application.OutputWriter;
import application.agent.model.Agency;
import application.agent.model.Agent;
import application.agent.server.AgentServer;
import application.util.RandomUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

import static application.constant.MessageConstants.NOT_OK;
import static application.constant.MessageConstants.OK;

public class AgentClient implements Runnable {
    private final OutputWriter outputWriter;
    private final Agent agent;
    private final AgentConfiguration agentConfiguration;
    private final AgentServer agentServer;
    private final AgentTimer agentTimer;

    public AgentClient(Agent agent, AgentConfiguration agentConfiguration, AgentServer agentServer) {
        this.agent = agent;
        this.agentConfiguration = agentConfiguration;
        this.agentServer = agentServer;
        this.agentTimer = new AgentTimer(agentConfiguration.getLowerBoundaryOfWait(), agentConfiguration.getUpperBoundaryOfWait());
        outputWriter = new OutputWriter(agent.getName() + " AgentClient");
    }


    @Override
    public void run() {
        agentTimer.startTimer();
        while (agent.isNotArrested()) {
            if (agentTimer.isTimeElapsed()) {
                connectToServer();
                agentTimer.resetTimer();
            }
        }
    }

    private void connectToServer() {
        int port = RandomUtils.generatePort(agentConfiguration.getLowerPortBoundary(), agentConfiguration.getUpperPortBoundary());
        outputWriter.print("Port próba: %s", port);
        try (Socket socket = new Socket("localhost", port); Scanner in = new Scanner(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String alias = receiveAlias(in);

            Agency agency = getAgencyBasedOnAlias(alias);
            sendAgencyAnswer(out, agency);

            if (!receivedAcknowledgement(in)) {
                return;
            }
            saveAgentToKnownAgents(alias, agency);

            if (agent.getAgency().equals(agency)) {
                handleSameAgency(in, out);
            } else {
                handleDifferentAgency(in, out);
            }

        } catch (IOException | TimeoutException e) {
        }

    }


    private void handleSameAgency(Scanner in, PrintWriter out) {
        sendOkSignal(out);

        receiveSecret(in);
        sendSecret(out);

    }

    private void sendSecret(PrintWriter out) {
        String randomSecret = agent.getRandomSecret();
        outputWriter.print("Random titok küldése a szervernek: %s.", randomSecret);
        out.println(randomSecret);
    }

    private void receiveSecret(Scanner in) {
        String randomSecretFromServer = in.nextLine();
        outputWriter.print("Titok fogadva a szervertől: %s.", randomSecretFromServer);
        agent.addSecret(randomSecretFromServer);

    }

    private void sendOkSignal(PrintWriter out) {
        outputWriter.print("A szerver ugyanahhoz az ügynökséghez tartozik mint ez a kliens, OK küldése!");
        out.println(OK);
    }

    private void handleDifferentAgency(Scanner in, PrintWriter out) {
        outputWriter.print("A szerver nem ugyanahhoz az ügynökséghez tartozik mint ez a kliens, ??? küldése!");

        out.println(NOT_OK);

    }

    private void saveAgentToKnownAgents(String alias, Agency agency) {
        outputWriter.print("Ügynök mentése ismert ügynökök közé: Ügynökség - %s, Álnév - %s", agency, alias);
        agent.putAgentToKnownAgents(alias, agency);
    }

    private boolean receivedAcknowledgement(Scanner in) {
        try {
            receiveResponseWithTimeOut(in);
            outputWriter.print("Az elküldött ügynökség helyes volt!");
            return true;
        } catch (TimeoutException e) {
            outputWriter.print("Az elküldött ügynökség helytelen volt!");
        }

        return false;
    }

    private Agency getAgencyBasedOnAlias(String alias) {
        Agency agencyOfAgent = agent.getAgencyOfAgent(alias);
        if (agencyOfAgent != null) {
            outputWriter.print("Kapott álnév az %s ügynökséghez tartozik.", agencyOfAgent);
            return agencyOfAgent;
        }

        outputWriter.print("Kapott álnév nem tartozik ismert ügynökhöz!");
        return guessAgency();
    }

    private void sendAgencyAnswer(PrintWriter out, Agency agencyOfAgent) {
        out.println(agencyOfAgent);
    }

    private Agency guessAgency() {
        Agency agencyByNumber = Agency.getAgencyByNumber(RandomUtils.getRandom(1, 2));
        outputWriter.print("Tippelt ügynökség küldése: %s", agencyByNumber);
        return agencyByNumber;
    }

    private String receiveAlias(Scanner in) throws TimeoutException {
        String alias = readAsync(in);
        outputWriter.print("Kapott álnév: %s", alias);
        return alias;
    }

    private String readAsync(Scanner in) throws TimeoutException {
        FutureTask<String> task = new FutureTask<>(in::nextLine);

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        try {
            return task.get(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            outputWriter.print("Hoppá");
            throw new TimeoutException();
        }
    }

    private String receiveResponseWithTimeOut(Scanner scanner) throws TimeoutException {
        FutureTask<String> readNextLine = new FutureTask<>(scanner::nextLine);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(readNextLine);

        try {
            return readNextLine.get(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new TimeoutException();
        }
    }
}
