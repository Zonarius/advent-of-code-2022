package party.zonarius.advent2022;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Grid {
    private GridCell[][] cells;

    private Grid() {
    }

    public static Grid parse(String[] input) {
        Grid grid = new Grid();
        List<List<GridCell>> cells = new ArrayList<>();

        int y = 0;
        for (String row : input) {
            int x = 0;
            List<GridCell> currentRow = new ArrayList<>();
            cells.add(currentRow);
            for (char raw : row.toCharArray()) {
                currentRow.add(new GridCell(grid, x++, y, raw - '0'));
            }
            y++;
        }

        grid.cells = cells.stream()
            .map(l -> l.toArray(GridCell[]::new))
            .toArray(GridCell[][]::new);
        return grid;
    }

    public Optional<GridCell> get(int x, int y) {
        if (x < 0 || y < 0 || x >= cells.length || y >= cells[0].length) {
            return Optional.empty();
        }
        return Optional.of(cells[y][x]);
    }

    public Stream<GridCell> cells() {
        return Stream.of(cells).flatMap(Stream::of);
    }
}
