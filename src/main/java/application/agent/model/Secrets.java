package application.agent.model;

import application.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class Secrets {

    private final List<Secret> secrets;

    public Secrets() {
        secrets = new ArrayList<>();
    }

    public void addSecret(String secretWord) {
        if (secrets.stream().noneMatch(secret -> secret.getSecret().equals(secretWord))) {
            secrets.add(new Secret(secretWord));
        }
    }

    @Override
    public String toString() {
        return "Secrets{" +
                "secrets=" + secrets +
                '}';
    }

    public String getRandomSecret() {
        return RandomUtils.getRandomElement(secrets).getSecret();
    }
}
