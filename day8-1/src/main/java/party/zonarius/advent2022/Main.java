package party.zonarius.advent2022;

import party.zonarius.advent2022.common.AdventInput;

public class Main {
    public static void main(String[] args) {
        String[] input = AdventInput.input().toArray(String[]::new);

        long count = Grid.parse(input).cells()
            .filter(GridCell::isVisible)
            .count();

        System.out.println(count);
    }
}