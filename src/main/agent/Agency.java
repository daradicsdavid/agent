package main.agent;

public enum Agency {
    First(1), Second(2);

    private final int number;

    Agency(int number) {

        this.number = number;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }
}
