package application.agent.model;

import java.util.ArrayList;
import java.util.List;

public class Secrets {

    private final List<Secret> secrets;

    public Secrets() {
        secrets = new ArrayList<>();
    }

    public void addSecret(String secretWord) {
        secrets.add(new Secret(secretWord));
    }

    @Override
    public String toString() {
        return "Secrets{" +
                "secrets=" + secrets +
                '}';
    }
}
