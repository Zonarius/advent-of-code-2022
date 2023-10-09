package party.zonarius.advent2022;

import java.util.Optional;
import java.util.stream.Stream;

public class GridCell {
    private final Grid grid;
    private final int x;
    private final int y;
    private final int height;

    private final Neighboring<Integer> effectiveHeight = new Neighboring<>();

    public GridCell(Grid grid, int x, int y, int height) {
        this.grid = grid;
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public Optional<GridCell> adjacent(Direction direction) {
        return grid.get(x + direction.offsetX(), y + direction.offsetY());
    }

    public boolean isVisible() {
        return Stream.of(Direction.values()).anyMatch(this::isVisibleFrom);
    }

    public boolean isVisibleFrom(Direction direction) {
        return neighborEffectiveHeight(direction) < height;
    }

    private int getEffectiveHeight(Direction direction) {
        Integer effH = effectiveHeight.get(direction);
        if (effH == null) {
            var neighbor = neighborEffectiveHeight(direction);
            effH = Math.max(neighbor, height);
            effectiveHeight.set(direction, effH);
        }
        return effH;
    }

    private Integer neighborEffectiveHeight(Direction direction) {
        return adjacent(direction)
            .map(cell -> cell.getEffectiveHeight(direction))
            .orElse(-1);
    }
}
