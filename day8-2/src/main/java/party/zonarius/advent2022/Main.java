package party.zonarius.advent2022;

import party.zonarius.advent2022.common.AdventInput;

public class Main {
    public static void main(String[] args) {
        String[] input = AdventInput.input().toArray(String[]::new);

        int score = Grid.parse(input).cells()
            .mapToInt(GridCell::scenicScore)
            .max().orElseThrow();

        System.out.println(score);
    }
}