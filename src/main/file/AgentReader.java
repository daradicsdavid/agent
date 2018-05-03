package main.file;

import main.OutputWriter;
import main.agent.Agency;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgentReader {

    public AgentFileData readAgentDataFromFile(Agency agency, int agentNumber) {
        String fileName = getFileName(agency, agentNumber);
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

            List<String> lines = stream.collect(Collectors.toList());

            validateLinesRead(lines);

            return new AgentFileData(getAgentAliases(lines.get(0)), lines.get(1));
        } catch (Exception e) {
            OutputWriter.print("Hiba történt a fájl olvasása során: %s", fileName);
            throw new RuntimeException();
        }
    }

    private List getAgentAliases(String aliasesString) {
        return Arrays.asList(aliasesString.split(" "));
    }

    private void validateLinesRead(List<String> lines) {
        if (lines.size() != 2) {
            OutputWriter.print("A fájl nem tartalmaz elég sort, sorok száma: %s", String.valueOf(lines.size()));
            throw new RuntimeException();
        }
    }

    private String getFileName(Agency agency, int agentNumber) {
        return String.format("agent%s-%s.txt", agency, String.valueOf(agentNumber));
    }
}
