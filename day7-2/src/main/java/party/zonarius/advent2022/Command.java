package party.zonarius.advent2022;

import party.zonarius.advent2022.common.Parser;

import java.util.List;

sealed interface Command permits Command.ChangeDirectory, Command.Ls {
    Parser<List<Command>> PARSER = Parser.<Command>choice(
        ChangeDirectory.PARSER,
        Ls.PARSER
    ).splitBy(Parser.NEWLINE);

    record ChangeDirectory(String name) implements Command {
        public static final Parser<ChangeDirectory> PARSER = Parser.string("$ cd ")
            .then(Parser.untilExclusive(Parser.NEWLINE))
            .map(ChangeDirectory::new);
    }

    record Ls(List<LsOutput> output) implements Command {
        public static final Parser<Ls> PARSER = Parser.string("$ ls").then(Parser.NEWLINE)
            .then(Parser.<LsOutput>choice(
                LsOutput.Directory.PARSER,
                LsOutput.File.PARSER
            ).splitBy(Parser.NEWLINE))
            .map(Ls::new);
    }

    sealed interface LsOutput permits LsOutput.Directory, LsOutput.File {
        record Directory(String name) implements LsOutput {
            public static final Parser<Directory> PARSER = Parser.string("dir ")
                .then(Parser.untilExclusive(Parser.NEWLINE))
                .map(Directory::new)
                .as("Ls output directory");
        }
        record File(String name, long size) implements LsOutput {
            public static final Parser<File> PARSER = Parser.seq(
                Parser.INTEGER,
                Parser.string(" "),
                Parser.untilExclusive(Parser.NEWLINE),
                (size, space, name) -> new File(name, size)
            ).as("Ls output file");
        }
    }
}