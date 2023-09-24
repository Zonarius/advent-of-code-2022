package party.zonarius.advent2022.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class ParserImpl<T> implements Parser<T> {
    private final String name;
    private final Function<ParserInput, ParseResult<T>> parserFunc;

    public ParserImpl(String name, Function<ParserInput, ParseResult<T>> parserFunc) {
        this.parserFunc = parserFunc;
        this.name = name;
    }

    @Override
    public T parse(String input) {
        return switch (parseResult(new ParserInput(input))) {
            case ParseResult.Success<T>(T result, var ignored) -> result;
            case ParseResult.Failure f -> throw new ParserException(f);
        };
    }

    @Override
    public T parseFile(String path) {
        try {
            return parse(Files.readString(Path.of(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ParseResult<T> parseResult(ParserInput input) {
        if (input.isEOF()) {
            return new ParseResult.Failure("EOF", input).asResult();
        }
        return parserFunc.apply(input);
    }

    @Override
    public <R> Parser<R> then(Parser<R> then) {
        return Parser.seq(this, then, (x, y) -> y);
    }

    @Override
    public <R> Parser<R> map(Function<T, R> mapper) {
        return Parser.create(name, input -> switch (parseResult(input)) {
            case ParseResult.Success<T>(var result, var rest) -> ParseResult.success(mapper.apply(result), rest);
            case ParseResult.Failure f -> f.asResult();
        });
    }

    @Override
    public <R> Parser<R> flatMap(Function<T, Parser<R>> mapper) {
        return Parser.create(name, input -> switch (parseResult(input)) {
            case ParseResult.Success<T>(var result, var rest) -> mapper.apply(result).parseResult(rest);
            case ParseResult.Failure f -> f.asResult();
        });
    }

    @Override
    public Parser<List<T>> splitBy(Parser<?> separator) {
        return Parser.seq(
            this.followedBy(separator).plus(),
            this,
            (list, last) -> Stream.concat(
                list.stream(),
                Stream.of(last)
            ).toList()
        );
    }

    @Override
    public Parser<T> followedBy(String str) {
        return followedBy(Parser.string(str));
    }

    @Override
    public Parser<T> followedBy(Parser<?> following) {
        return Parser.seq(this, following, (x, y) -> x);
    }

    @Override
    public Parser<List<T>> plus() {
        return Parser.create("(%s)+".formatted(name), input -> {
            List<T> results = new ArrayList<>();
            ParseResult.Failure failure = null;
            while (failure == null) {
                switch (parseResult(input)) {
                    case ParseResult.Success<T>(T result, var rest) -> {
                        results.add(result);
                        input = rest;
                    }
                    case ParseResult.Failure f -> failure = f;
                }
            }
            if (results.isEmpty()) {
                return failure.asResult();
            } else {
                return ParseResult.success(results, input);
            }
        });
    }

    @Override
    public Parser<T> as(String name) {
        return Parser.create(name, parserFunc);
    }

    @Override
    public String toString() {
        return name;
    }
}
