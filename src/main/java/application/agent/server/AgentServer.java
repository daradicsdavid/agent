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

import static application.constant.MessageConstants.NOT_OK;
import static application.constant.MessageConstants.OK;

public class AgentServer implements Runnable {

    private final OutputWriter outputWriter;
    private final Agent agent;
    private final AgentConfiguration agentConfiguration;
    private volatile Integer currentPort;
    private final Object portLock = new Object();

    public AgentServer(Agent agent, AgentConfiguration agentConfiguration) {
        this.agent = agent;
        this.agentConfiguration = agentConfiguration;
        outputWriter = new OutputWriter(agent.getName() + " AgentServer");
    }

    @Override
    public void run() {
        while (agent.isNotArrested()) {
            ServerSocket serverSocket = openServerSocket();
            acceptRequest(serverSocket);
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private void acceptRequest(ServerSocket serverSocket) {
        try {
            Socket clientSocket = serverSocket.accept();
            outputWriter.print("%s - %s", clientSocket.getLocalPort(), currentPort);

            handleClient(clientSocket);
            clientSocket.close();
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

            String response = receiveResponse(in);

            if (response.equals(OK)) {
                handleSameAgency(in, out);
            } else if (response.equals(NOT_OK)) {

            }

        } catch (IOException e) {

        }


    }

    private void handleSameAgency(Scanner in, PrintWriter out) {
        outputWriter.print("A kliens is ugyanahhoz az ügynökséghez tartozik mint ez a szerver!");
        sendSecret(out);
        receiveSecret(in);
    }

    private void receiveSecret(Scanner in) {
        String randomSecretFromClient = in.nextLine();
        outputWriter.print("Titok fogadva a klienstől: %s.", randomSecretFromClient);
        agent.addSecret(randomSecretFromClient);

    }

    private void sendSecret(PrintWriter out) {
        String randomSecret = agent.getRandomSecret();
        outputWriter.print("Random titok küldése a kliensnek: %s.", randomSecret);
        out.println(randomSecret);
    }

    private String receiveResponse(Scanner in) {
        String response = in.nextLine();
        outputWriter.print("Kapott üzenet: %s", response);
        return response;
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

    private ServerSocket openServerSocket() {
        synchronized (portLock) {
            currentPort = null;
            ServerSocket serverSocket = null;
            while (getCurrentPort() == null) {
                try {
                    currentPort = RandomUtils.generatePort(agentConfiguration.getLowerPortBoundary(), agentConfiguration.getUpperPortBoundary());
                    serverSocket = new ServerSocket(getCurrentPort());
                    serverSocket.setSoTimeout(agentConfiguration.getUpperBoundaryOfWait());
                    outputWriter.print("Kapott port: %s", getCurrentPort());
                } catch (IOException e) {
                    currentPort = null;
                }
            }
            return serverSocket;
        }
    }


    public synchronized Integer getCurrentPort() {
        synchronized (portLock) {
            return currentPort;
        }
    }
}
