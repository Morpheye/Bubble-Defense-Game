package cyv.app.render.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import cyv.app.BubbleGame;
import cyv.app.game.Level;

public class TileRenderer {
    /**
     * Renders a tilemap of a level. Assumes the batcher is currently drawing.
     * @param batch Sprite batcher
     * @param grid Tile grid
     * TODO: add support for multiple themes
     */
    public static void renderTiles(BubbleGame game, SpriteBatch batch, int[][] grid) {
        // temp: theme is always "grass"
        final String THEME = "grass";
        TextureRegion[][] tiles = game.getAssets().getTileMap(THEME);

        // loop through the grid of tiles
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y] == 1) {
                    float size = Level.TILE_SIZE;
                    float worldX = x * size;
                    float worldY = y * size; // bottom-left origin
                    batch.draw(getTile(tiles, grid, x, y), worldX, worldY,
                        size, size);
                }
            }
        }
    }

    private static TextureRegion getTile(TextureRegion[][] tileMap, int[][] grid, int x, int y) {
        // detect whether tile has grass
        if (isSolid(grid, x, y + 1)) {
            // tile above this one is solid, now check the borders
            boolean leftBorder = isSolid(grid, x - 1, y);
            boolean rightBorder = isSolid(grid, x + 1, y);

            if (leftBorder && rightBorder) return tileMap[3][0];
            else if (leftBorder) return tileMap[2][1];
            else if (rightBorder) return tileMap[2][0];
            else return tileMap[1][3];
        } else {
            // tile above this one isn't solid, now check the sides
            boolean leftBorder = isSolid(grid, x - 1, y);
            boolean rightBorder = isSolid(grid, x + 1, y);
            boolean leftTopBorder = isSolid(grid, x - 1, y + 1);
            boolean rightTopBorder = isSolid(grid, x + 1, y + 1);

            if (leftBorder && rightBorder) {
                if (leftTopBorder && rightTopBorder) {
                    return tileMap[0][3];
                } else if (leftTopBorder) {
                    return tileMap[0][0];
                } else if (rightTopBorder) {
                    return tileMap[0][1];
                } else return tileMap[0][2];
            } else if (leftBorder) {
                if (leftTopBorder) return tileMap[2][3];
                else return tileMap[1][1];
            } else if (rightBorder) {
                if (rightTopBorder) return tileMap[2][2];
                else return tileMap[1][0];
            } else {
                return tileMap[1][2];
            }
        }
    }

    private static boolean isSolid(int[][] grid, int x, int y) {
        if (x < 0 || x >= grid.length || y < 0 || y >= grid[x].length) return false;
        return grid[x][y] == 1;
    }
}
