package party.zonarius.advent2022;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<Command> commands = Command.PARSER.parseInput();
        Node.Folder currentDir = null;
        for (Command command : commands) {
            switch (command) {
                case Command.ChangeDirectory(String name) -> {
                    if (name.equals("/")) {
                        currentDir = Node.createRoot();
                    } else if (name.equals("..")) {
                        currentDir = currentDir.parent();
                    } else {
                        currentDir = currentDir.addFolder(name);
                    }
                }
                case Command.Ls(var output) -> {
                    for (Command.LsOutput lsOutput : output) {
                        switch (lsOutput) {
                            case Command.LsOutput.File(var name, var size) -> {
                                currentDir.addFile(name, size);
                            }
                            case Command.LsOutput.Directory d -> {}
                        }
                    }
                }
            }
        }
        long totalSpace = 70_000_000;
        long freeSpaceTarget = 30_000_000;
        Node.Folder root = currentDir.getRoot();
        long freeSpace = totalSpace - root.totalSize();
        long toDelete = freeSpaceTarget - freeSpace;

        long min = root.descendantFolders()
            .filter(f -> f.totalSize() >= toDelete)
            .mapToLong(Node::totalSize)
            .min().orElseThrow();
        System.out.println(min);
    }

    public static sealed abstract class Node {
        protected final String name;
        protected final Folder parent;
        protected long totalSize = 0;

        protected Node(Folder parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        protected void addSize(long size) {
            totalSize += size;
            if (parent != null) {
                parent.addSize(size);
            }
        }

        public long totalSize() {
            return totalSize;
        }

        public abstract Folder getRoot();

        public static final class Folder extends Node {
            private final List<Node> children;
            Folder(Folder parent, String name) {
                super(parent, name);
                children = new ArrayList<>();
            }

            @Override
            public Folder getRoot() {
                if (parent() == null) {
                    return this;
                } else {
                    return parent().getRoot();
                }
            }

            public Folder addFolder(String name) {
                Folder folder = new Folder(this, name);
                children.add(folder);
                return folder;
            }

            public File addFile(String name, long size) {
                File file = new File(this, name, size);
                children.add(file);
                return file;
            }

            public Stream<Node> descendants() {
                return Stream.concat(
                    children.stream(),
                    children.stream().flatMap(node -> switch (node) {
                        case Folder f -> f.descendants();
                        case File f -> Stream.of(f);
                    })
                );
            }

            public Stream<Folder> descendantFolders() {
                return descendants()
                    .filter(n -> n instanceof Folder)
                    .map(n -> (Folder) n);
            }
        }

        public static final class File extends Node {
            File(Folder parent, String name, long size) {
                super(parent, name);
                addSize(size);
            }

            @Override
            public Folder getRoot() {
                return parent().getRoot();
            }
        }

        public Folder parent() {
            return parent;
        }

        static public Folder createRoot() {
            return new Folder(null, "/");
        }
    }
}