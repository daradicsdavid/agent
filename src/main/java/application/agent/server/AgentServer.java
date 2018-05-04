package application.agent.server;

import application.AgentConfiguration;
import application.OutputWriter;
import application.agent.model.Agency;
import application.agent.model.Agent;
import application.util.NumberUtils;
import application.util.RandomUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AgentServer implements Runnable {
    private static final String OK = "OK";
    private final OutputWriter outputWriter;
    private final Agent agent;
    private final AgentConfiguration agentConfiguration;
    private Integer currentPort;

    public AgentServer(Agent agent, AgentConfiguration agentConfiguration) {
        this.agent = agent;
        this.agentConfiguration = agentConfiguration;
        outputWriter = new OutputWriter(agent.getName() + " AgentServer");
    }

    @Override
    public void run() {
        while (agent.isNotArrested()) {
            ServerSocket serverSocket = openServerSocker();
            acceptRequest(serverSocket);

        }
    }

    private void acceptRequest(ServerSocket serverSocket) {
        try {
            Socket clientSocket = serverSocket.accept();
            handleClient(clientSocket);
            serverSocket.close();
        } catch (IOException e) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            sendRandomAlias(out);

            Agency agencyGuess = receiveAgencyGuess(in);

            if (!agent.getAgency().equals(agencyGuess)) {
                outputWriter.print("Helytelen ügynökség tipp: %s. A kapcsolat bontásra kerül!", agencyGuess);
                return;
            }

            sendOkSignal(out);

        } catch (IOException e) {

        }


    }

    private void sendOkSignal(PrintWriter out) {
        outputWriter.print("Helyes ügynökség tipp, OK küldése!");
        out.println(OK);
    }

    private Agency receiveAgencyGuess(Scanner in) {
        try {
            String agencyGuess = in.nextLine();
            int agencyNumber = NumberUtils.toNumber(agencyGuess);
            Agency agency = Agency.getAgencyByNumber(agencyNumber);
            outputWriter.print("Kapott ügynökség tipp: %s", agency);
            return agency;
        } catch (Exception e) {
            return null;
        }

    }

    private void sendRandomAlias(PrintWriter out) {
        String randomAlias = agent.getRandomAlias();
        outputWriter.print("Álnév küldése: %s", randomAlias);
        out.println(randomAlias);
    }

    private ServerSocket openServerSocker() {
        setCurrentPort(null);
        ServerSocket serverSocket = null;
        while (getCurrentPort() == null) {
            try {
                setCurrentPort(RandomUtils.generatePort(agentConfiguration.getLowerPortBoundary(), agentConfiguration.getUpperPortBoundary()));
                serverSocket = new ServerSocket(currentPort);
                serverSocket.setSoTimeout(agentConfiguration.getUpperBoundaryOfWait());
            } catch (IOException e) {
                setCurrentPort(null);
            }
        }
        return serverSocket;
    }

    private synchronized void setCurrentPort(Integer portNumber) {
        currentPort = portNumber;
    }

    public synchronized Integer getCurrentPort() {
        return currentPort;
    }
}
