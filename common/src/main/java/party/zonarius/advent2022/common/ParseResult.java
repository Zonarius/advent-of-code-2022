package party.zonarius.advent2022.common;

public sealed interface ParseResult<T> {
    static <T> Success<T> success(T result, ParserInput input) {
        return new Success<>(result, input);
    }
    @SuppressWarnings("unchecked")
    static <T> ParseResult<T> failure(String errorMessage, ParserInput input) {
        return (ParseResult<T>) new Failure(errorMessage, input);
    }
    record Success<T>(T result, ParserInput input) implements ParseResult<T> {}
    record Failure(String errorMessage, ParserInput input) implements ParseResult<Object> {
        @SuppressWarnings("unchecked")
        public <T> ParseResult<T> asResult() {
            return (ParseResult<T>) this;
        }
    }
}
