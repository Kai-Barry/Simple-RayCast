package com.mygdx.game.tile;

import java.util.ArrayList;
import java.util.List;

import static com.mygdx.game.tile.TileType.*;

public class TileSet {
    private int width;
    private int height;
    private  List<List<Tile>> tileMap;

    public TileSet(int x, int y) {
        this.width = x;
        this.height = y;
        this.tileMap = this.setupMap();
    }

    private List<List<Tile>> setupMap() {
        if (checkXY()) {
            List<List<Tile>> mapCreation = new ArrayList<>();
            for (int y = 0; y < this.height; y++) {
                mapCreation.add(new ArrayList<Tile>());
                for (int x = 0; x < this.width; x++) {
                    if (x == 0 || x == this.width - 1 || y == 0 || y == this.height - 1) {
                        mapCreation.get(y).add(new Tile(WALL));
                    } else {
                        mapCreation.get(y).add(new Tile(EMPTY));
                    }
                }
            }
            mapCreation.get(4).get(4).changeTileType(WALL);
            mapCreation.get(4).get(5).changeTileType(WALL);
            mapCreation.get(4).get(6).changeTileType(WALL);
            mapCreation.get(3).get(4).changeTileType(WALL);
            return mapCreation;
        }
        return null;
    }

    private Boolean checkXY() {
        if (this.width <= 3 || this.height <= 3) {
            System.out.println("NOT GOOD");
            return false;
        }
        return true;
    }

    public Integer GetIndexOf(int x, int y) {
        if (x >= this.width || x < 0 || y >=this.height || y < 0) {
            return -1;
        }
        return x + (y * this.height);
    }

    public int[] getXYOf(int index) {
        int coordinates[]= new int[2];
        if (index > this.height*this.width || index < 0) {
            coordinates[0] = -1;
            coordinates[1] = -1;
            return coordinates;
        }
        int x = index;
        int y = 0;
        while (x >= this.width) {
            x -= this.width;
            y++;
        }
        coordinates[0] = x;
        coordinates[1] = y;
        return  coordinates;
    }

    public Tile tileAtXY(int x, int y) {
        return this.tileMap.get(y).get(x);
    }

    public Tile tileAtIndex(int index) {
        int x = this.getXYOf(index)[0];
        int y = this.getXYOf(index)[1];
        return this.tileMap.get(y).get(x);
    }



    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}
