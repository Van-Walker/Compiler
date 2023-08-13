package utils;

public class MyError extends RuntimeException {
    public Position position;
    public String message;

    public MyError(Position position, String message) {
        this.position = position;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error at " + position.toString() + " : " + message;
    }

}
