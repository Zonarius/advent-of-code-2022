package party.zonarius.advent2022.common;

public class ParserException extends RuntimeException {
    public ParserException(ParseResult.Failure failure) {
        super(createMessage(failure));
    }

    private static String createMessage(ParseResult.Failure failure) {
        return createMessage(failure, 0);
    }

    private static String createMessage(ParseResult.Failure failure, int depth) {
        ErrorLine errorLine = ErrorLine.errorLine(failure.input());
        StringBuilder sb = new StringBuilder();
        sb.append("Parser error at index %d: %s\n%s\n%s".formatted(
            failure.input().index(),
            failure.errorMessage(),
            errorLine.line(),
            errorLine.createArrow()
        ));
        if (!failure.reasons().isEmpty()) {
            for (int i = 0; i < failure.reasons().size(); i++) {
                ParseResult.Failure subfail = failure.reasons().get(i);
                sb.append("\nReason %d:\n".formatted(i + 1));
                sb.append(createMessage(subfail, depth + 1));
            }
        }
        return sb.toString().indent(depth * 2);
    }

    private record ErrorLine(String line, int errorIndex) {
        public static ErrorLine errorLine(ParserInput input) {
            String inp = input.input();
            int from = 0;
            while (true) {
                int next = inp.indexOf('\n', from);
                if (next < 0) {
                    return new ErrorLine(inp.substring(from), input.index() - from);
                }
                if (next > input.index()) {
                    return new ErrorLine(inp.substring(from, next), input.index() - from);
                }
                from = next + 1;
            }
        }

        public String createArrow() {
            return "-".repeat(errorIndex) + "^";
        }
    }
}