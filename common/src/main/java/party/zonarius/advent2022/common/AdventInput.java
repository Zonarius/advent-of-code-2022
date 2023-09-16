package party.zonarius.advent2022.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class AdventInput {
    private AdventInput() {}

    public static Stream<String> testInput() {
        try {
            return Files.lines(Path.of("./test-input"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<String> input() {
        try {
            return Files.lines(Path.of("./input"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}