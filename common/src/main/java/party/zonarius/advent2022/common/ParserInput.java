package party.zonarius.advent2022.common;

public record ParserInput(String input, int index) {
    public ParserInput(String input) {
        this(input, 0);
    }

    public boolean startsWith(String expected) {
        return input.substring(index).startsWith(expected);
    }

    public ParserInput substring(int beginIndex) {
        return new ParserInput(input, index + beginIndex);
    }

    public String substring(int beginIndex, int endIndex) {
        return input.substring(index + beginIndex, index + endIndex);
    }

    public char charAt(int i) {
        return input.charAt(index + i);
    }

    public boolean isEOF() {
        return index >= input.length();
    }

    public int indexOf(String substring) {
        return input.indexOf(substring, index);
    }
}
