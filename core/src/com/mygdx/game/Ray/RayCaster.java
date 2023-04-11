package com.mygdx.game.Ray;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.tile.TileSet;
import com.mygdx.game.tile.TileType;
import com.mygdx.game.utility.UserSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RayCaster {
    private double playerA;
    private double playerX;
    private double playerY;
    private TileSet map;
    private int gridSize;
    private List<ShapeRenderer> lines;
    private List<ShapeRenderer> walls;
    private List<Float> raysX;
    private List<Float> raysY;
    private List<Float> distant;
    private List<Boolean> VH;
    private static double deg = 0.0174533;
    private UserSettings userSettings;

    public RayCaster(double playerA, double playerX, double playerY, int gridSize , TileSet map, UserSettings userSettings) {
        this.playerA = playerA;
        this.playerX = playerX;
        this.playerY = playerY;
        this.map = map;
        this.gridSize = gridSize;
        float empty[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        this.raysX = new ArrayList(Arrays.asList(empty));
        this.raysY = new ArrayList(Arrays.asList(empty));
        this.distant = new ArrayList(Arrays.asList(empty));
        this.VH = new ArrayList(Arrays.asList(empty));

    }

    public void updatePlayerLoc(double playerA, double playerX, double playerY) {
        this.playerA = playerA;
        this.playerX = playerX;
        this.playerY = playerY;
    }

    public void createRays(int numRays) {
        boolean hitH = false;
        boolean hitV = false;
        int mapX = 0;
        int mapY = 0;
        int dof = 0;
        double rayXHor = 0;
        double rayYHor = 0;
        double rayXVer = 0;
        double rayYVer = 0;
        double offsetX = 100;
        double offsetY = 100;
        this.raysX.clear();
        this.raysY.clear();
        this.distant.clear();
        this.VH.clear();

        //ray angles
        double rayA = this.playerA - deg * 30; // - 30 * deg;
        if (rayA < 0) {
            rayA += 2 * Math.PI;
        } else if (rayA > 2 * Math.PI) {
            rayA -= 2 * Math.PI;
        }
        //numrays
        for (int r = 0; r < numRays; r++) {
            //horizontal check
            double tanA = -1 / Math.tan(rayA);
            if (rayA == 0 || rayA == Math.PI) {
                rayXHor = this.playerX;
                rayYHor = this.playerY;
                dof = map.getWidth() + map.getHeight();
            } else if (rayA > Math.PI) { //looking down
                rayYHor = (int) (playerY / gridSize) * gridSize - 0.0001;
                rayXHor = (playerY - rayYHor) * tanA + playerX;
                offsetY = -1 * gridSize;
                offsetX = -offsetY * tanA;
            } else if (rayA < Math.PI) { //looking up
                rayYHor = (int) (playerY / gridSize) * gridSize + gridSize;
                rayXHor = (playerY - rayYHor) * tanA + playerX;
                offsetY = gridSize;
                offsetX = -offsetY * tanA;
            }
            while (dof < 20) {
                mapX = (int) Math.floor(rayXHor / gridSize);
                mapY = (int) Math.floor(rayYHor / gridSize);
                if (mapY >= 0 && mapX >= 0 && mapX < map.getWidth() && mapY < map.getWidth()) {
                    if (map.tileAtXY(mapX, mapY).getTileType() == TileType.WALL) {
                        hitH = true;
                        break;
                    }
                }
                rayXHor += offsetX;
                rayYHor += offsetY;
                dof++;
            }
            //vertical check
            double tanB = -Math.tan(rayA);
            if (rayA == 0 || rayA == Math.PI) {
                rayXVer = this.playerX;
                rayYVer = this.playerY;
                dof = map.getWidth() + map.getHeight();
            } else if (rayA > Math.PI / 2 && rayA < 3 * Math.PI / 2) { //looking left
                rayXVer = (int) (playerX / gridSize) * gridSize - 0.0001;
                rayYVer = (playerX - rayXVer) * tanB + playerY;
                offsetX = -1 * gridSize;
                offsetY = -offsetX * tanB;
            } else if (rayA < Math.PI / 2 || rayA > 3 * Math.PI / 2) { //looking right
                rayXVer = (int) (playerX / gridSize) * gridSize + gridSize;
                rayYVer = (playerX - rayXVer) * tanB + playerY;
                offsetX = gridSize;
                offsetY = -offsetX * tanB;
            }
            dof = 0;
            while (dof < 20) {
                mapX = (int) Math.floor(rayXVer / gridSize);
                mapY = (int) Math.floor(rayYVer / gridSize);
                if (mapY >= 0 && mapX >= 0 && mapX < map.getWidth() && mapY < map.getWidth()) {
                    if (map.tileAtXY(mapX, mapY).getTileType() == TileType.WALL) {
                        hitV = true;
                        break;
                    }
                }
                rayXVer += offsetX;
                rayYVer += offsetY;
                dof++;
            }
            this.setOptimalRay(rayXVer, rayYVer, rayXHor, rayYHor, hitH, hitV);
            rayA += deg;
            if (rayA < 0) {
                rayA += 2 * Math.PI;
            } else if (rayA > 2 * Math.PI) {
                rayA -= 2 * Math.PI;
            }
        }
    }
    private double dist(double ax, double ay, double bx, double by ) {
        return Math.sqrt(((bx - ax) * (bx - ax)) + ((by - ay) * (by - ay)));
    }

    private void setOptimalRay(double xVer,double yVer,double xHor,double yHor,boolean hitH, boolean hitV) {
        if (!hitH && hitV) {
//            System.out.println("Vertical Hit 1");
            this.raysX.add((float)xVer);
            this.raysY.add((float)yVer);
            this.distant.add((float)dist(this.playerX, this.playerY, xVer, yVer));
            this.VH.add(false);
        } else if (!hitV && hitH) {
//            System.out.println("Horizontal Hit 1");
            this.raysX.add((float)xHor);
            this.raysY.add((float)yHor);
            this.distant.add((float)dist(this.playerX, this.playerY, xHor, yHor));
            this.VH.add(true);
        } else {
            if (dist(this.playerX, this.playerY, xVer, yVer) <= dist(this.playerX, this.playerY, xHor, yHor)) {
//                System.out.println("Vertical Hit 2: " + dist(this.playerX, this.playerY, xVer, yVer) + "<" + dist(this.playerX, this.playerY, xHor, yHor));
                this.raysX.add((float)xVer);
                this.raysY.add((float)yVer);
                this.distant.add((float)dist(this.playerX, this.playerY, xVer, yVer));
                this.VH.add(false);
            } else {
//                System.out.println("Horizontal Hit 2:  " +  dist(playerX, playerY, xHor, yHor) + "<" + dist(playerX, playerY, xVer, yVer));
                this.raysX.add((float)xHor);
                this.raysY.add((float)yHor);
                this.distant.add((float)dist(this.playerX, this.playerY, xHor, yHor));
                this.VH.add(true);
            }
        }
    }

    public void createRayRenderers(int numRays) {
        this.lines = new ArrayList<>();
        for (int y = 0; y < numRays; y++) {
            lines.add(new ShapeRenderer());
        }
    }

    public void drawRays() {
        int rayNum = 0;
        if (raysX.size() > 0) {
            for (ShapeRenderer line : lines) {
                line.begin(ShapeRenderer.ShapeType.Line);
                line.setColor(1, 0, 0, 1);
                line.line((float) playerX, (float) playerY, raysX.get(rayNum), raysY.get(rayNum));
                line.end();
                rayNum++;
            }
        }
    }

    public void createWalls(int numRays) {
        this.walls = new ArrayList<>();
        for (int y = 0; y < numRays; y++) {
            walls.add(new ShapeRenderer());
        }
    }

    public void draw3D() {
        int rayNum = 0;
        int screenH = 720;
        int screenW = 1280;
        int shift = 1280/60;
        if (raysX.size() > 0) {
            for (ShapeRenderer wall : this.walls) {
                if (!this.distant.get(rayNum).isNaN()) {
                    System.out.println(this.distant.get(rayNum));
                    double lineH = (this.gridSize * screenH) / (this.distant.get(rayNum));
                    if (lineH > screenW) {
                        lineH = screenW;
                    }
                    double lineO = screenH - (lineH/4);

                    wall.begin(ShapeRenderer.ShapeType.Filled);
                    if (this.VH.get(rayNum) == true) {
                        wall.setColor(0.9f, 0.9f, 0, 0f);
                    } else {
                        wall.setColor(0.6f, 0.9f, 0, 0f);
                    }
                    wall.rectLine(new Vector2(rayNum * shift, (float)lineO- (screenH/2)), new Vector2(rayNum * shift, (float) (lineH + lineO) - (screenH/2)), shift);
                    wall.end();
                }
                rayNum++;
            }
        }
    }
}
