package party.zonarius.advent2022;

import party.zonarius.advent2022.common.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Main {
    record Input(List<Stack<String>> stacks, List<Instruction> instructions) {
        public static Parser<Input> parser() {
            return Parser.seq(
                stacksParser(),
                Parser.NEWLINE.then(Instruction.parser().splitBy(Parser.NEWLINE)),
                Input::new
            );
        }

        private static Parser<List<Stack<String>>> stacksParser() {
            return StackElement.parser().splitBy(" ").followedBy(Parser.NEWLINE).plus()
                .followedBy(Parser.WHITESPACE.then(Parser.INTEGER.then(Parser.WHITESPACE).plus()))
                .followedBy(Parser.NEWLINE)
                .map(lines -> {
                    int stackAmount = lines.stream().mapToInt(List::size).max().orElseThrow();
                    List<Stack<String>> stacks = new ArrayList<>(stackAmount);
                    for (int i = 0; i < stackAmount; i++) {
                        stacks.add(new Stack<>());
                    }
                    for (int i = lines.size() - 1; i >= 0; i--) {
                        var line = lines.get(i);
                        for (int j = 0; j < line.size(); j++) {
                            switch (line.get(j)) {
                                case Nothing ignored -> {}
                                case Crate(var label) -> stacks.get(j).add(label);
                            }
                        }
                    }
                    return stacks;
                });
        }
    }
    sealed interface StackElement permits Nothing, Crate {
        static Parser<StackElement> parser() {
            return Parser.choice(
                Parser.string("   ").as("Nothing").map(x -> Nothing.INSTANCE),
                Parser.string("[").then(Parser.anyString(1)).followedBy("]").as("Crate").map(Crate::new)
            );
        }
    }
    final static class Nothing implements StackElement {
        public static Nothing INSTANCE = new Nothing();
        private Nothing() {}
    }

    record Crate(String label) implements StackElement {}
    record Instruction(int amount, int from, int to) {
        public static Parser<Instruction> parser() {
            return Parser.seq(
                Parser.string("move ").then(Parser.INTEGER),
                Parser.string(" from ").then(Parser.INTEGER),
                Parser.string(" to ").then(Parser.INTEGER),
                Instruction::new
            );
        }
    }

    public static void main(String[] args) {
        Input input = Input.parser().parseFile("./input");
        List<Stack<String>> stacks = input.stacks();
        for (Instruction instruction : input.instructions) {
            Stack<String> tmpStack = new Stack<>();
            for (int i = 0; i < instruction.amount(); i++) {
                tmpStack.add(stacks.get(instruction.from() - 1).pop());
            }
            for (int i = 0; i < instruction.amount(); i++) {
                stacks.get(instruction.to() - 1).add(tmpStack.pop());
            }
        }

        System.out.println(stacks.stream().map(Stack::peek).collect(Collectors.joining()));
    }

}

