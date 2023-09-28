package party.zonarius.advent2022.common;

import java.util.List;

public sealed interface ParseResult<T> {
    static <T> Success<T> success(T result, ParserInput input) {
        return new Success<>(result, input);
    }

    static <T> ParseResult<T> failure(String errorMessage, ParserInput input) {
        return failure(errorMessage, input, List.of());
    }

    static <T> ParseResult<T> failure(String errorMessage, ParserInput input, Failure reason) {
        return failure(errorMessage, input, List.of(reason));
    }

    static <T> ParseResult<T> failure(String errorMessage, ParserInput input, List<Failure> reasons) {
        return new Failure(errorMessage, input, reasons).asResult();
    }

    record Success<T>(T result, ParserInput input) implements ParseResult<T> {}
    record Failure(String errorMessage, ParserInput input, List<Failure> reasons) implements ParseResult<Object> {
        @SuppressWarnings("unchecked")
        public <T> ParseResult<T> asResult() {
            return (ParseResult<T>) this;
        }
    }
}
