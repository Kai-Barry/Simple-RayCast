package com.mygdx.game.tile;

public class Tile {
    private TileType tileType;
    public Tile (TileType initialType) {
        this.tileType = initialType;
    }

    public TileType getTileType() {
        return tileType;
    }
    public TileType changeTileType(TileType newTileType) {
        TileType previousTileType = this.tileType;
        this.tileType = newTileType;
        return previousTileType;
    }
}
