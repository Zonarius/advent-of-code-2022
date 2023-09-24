package party.zonarius.advent2022.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Parser<T> {
    T parse(String input);
    T parseFile(String path);
    ParseResult<T> parseResult(ParserInput input);

    <R> Parser<R> then(Parser<R> then);

    <R> Parser<R> map(Function<T, R> mapper);
    <R> Parser<R> flatMap(Function<T, Parser<R>> mapper);
    Parser<List<T>> splitBy(Parser<?> separator);
    Parser<T> as(String name);

    Parser<T> followedBy(String str);
    Parser<T> followedBy(Parser<?> str);

    Parser<List<T>> plus();

    static <T> Parser<T> create(String name, Function<ParserInput, ParseResult<T>> parser) {
        return new ParserImpl<>(name, parser);
    }

    static <T> Parser<T> failing(ParseResult.Failure f) {
        return create("Always Failing", input -> f.asResult());
    }

    static <T> Parser<List<T>> seq(List<Parser<T>> parsers) {
        return create("Seq %s".formatted(parsers), input -> {
            List<T> results = new ArrayList<>();
            ParseResult.Failure failure = null;
            for (Parser<T> parser : parsers) {
                switch (parser.parseResult(input)) {
                    case ParseResult.Success<T>(T result, var rest) -> {
                        results.add(result);
                        input = rest;
                    }
                    case ParseResult.Failure f -> failure = f;
                }
                if (failure != null) {
                    break;
                }
            }
            if (failure != null) {
                return failure.asResult();
            } else {
                return ParseResult.success(results, input);
            }
        });
    }

    @SuppressWarnings("unchecked")
    static <T1, T2, R> Parser<R> seq(Parser<T1> p1, Parser<T2> p2, BiFunction<T1, T2, R> mapper) {
        return seq(List.of(
            (Parser<Object>)p1,
            (Parser<Object>)p2
        )).map(results -> mapper.apply(
            (T1) results.get(0),
            (T2) results.get(1)
        ));
    }

    @SuppressWarnings("unchecked")
    static <T1, T2, T3, R> Parser<R> seq(Parser<T1> p1, Parser<T2> p2, Parser<T3> p3, Function3<T1, T2, T3, R> mapper) {
        return seq(List.of(
            (Parser<Object>)p1,
            (Parser<Object>)p2,
            (Parser<Object>)p3
        )).map(results -> mapper.apply(
            (T1) results.get(0),
            (T2) results.get(1),
            (T3) results.get(2)
        ));
    }

    static <T> Parser<T> choice(List<Parser<T>> parsers) {
        return create("Choice %s".formatted(parsers), input -> {
            for (Parser<T> parser : parsers) {
                switch (parser.parseResult(input)) {
                    case ParseResult.Success<T> s -> {
                        return s;
                    }
                    case ParseResult.Failure f -> {}
                }
            }
            return ParseResult.failure("No choice matched. Expected one of %s".formatted(parsers), input);
        });
    }

    static <T> Parser<T> choice(Parser<T> ...parsers) {
        return choice(Arrays.asList(parsers));
    }

    static Parser<String> string(String expected) {
        return Parser.create("Constant String \"%s\"".formatted(escape(expected)), input -> {
            if (input.startsWith(expected)) {
                return ParseResult.success(expected, input.substring(expected.length()));
            } else {
                return ParseResult.failure("Expected \"%s\"".formatted(escape(expected)), input);
            }
        });
    }

    static Parser<String> anyString(int length) {
        return Parser.create("Parsing any string of length %d".formatted(length), input -> {
            return ParseResult.success(input.substring(0, length), input.substring(length));
        });
    }

    static Parser<Integer> digit() {
        return Parser.create("Digit", input -> {
            char c = input.charAt(0);
            if (Character.isDigit(c)) {
                return ParseResult.success(c - '0', input.substring(1));
            } else {
                return ParseResult.failure("Expected digit", input);
            }
        });
    }

    Parser<Integer> INTEGER = digit().plus().map(digits -> {
        int sum = 0;
        for (int i : digits) {
            sum = (sum * 10) + i;
        }
        return sum;
    });

    @SuppressWarnings("unchecked")
    Parser<String> WHITESPACE = Parser.choice(
        Parser.string(" "),
        Parser.string("\t"),
        Parser.string("\r")
    ).plus().map(l -> String.join("", l));

    @SuppressWarnings("unchecked")
    Parser<String> NEWLINE = Parser.choice(
        Parser.string("\r\n"),
        Parser.string("\r"),
        Parser.string("\n")
    ).as("NEWLINE");

    private static String escape(String s){
        return s.replace("\\", "\\\\")
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\f", "\\f")
            .replace("\"", "\\\"");
    }

    default Parser<List<T>> splitBy(String separator) {
        return splitBy(Parser.string(separator));
    }
}
