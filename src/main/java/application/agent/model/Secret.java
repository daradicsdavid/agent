package application.agent.model;

public class Secret {
    private final String secret;

    public Secret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return "Secret{" +
                "secret='" + secret + '\'' +
                '}';
    }


}
