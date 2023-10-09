package party.zonarius.advent2022;

public class Neighboring<T> {
    private T up;
    private T down;
    private T left;
    private T right;

    public T get(Direction direction) {
        return switch (direction) {
            case UP -> up;
            case DOWN -> down;
            case LEFT -> left;
            case RIGHT -> right;
        };
    }

    public void set(Direction direction, T value) {
        switch (direction) {
            case UP -> up = value;
            case DOWN -> down = value;
            case LEFT -> left = value;
            case RIGHT -> right = value;
        }
    }
}
