package com.mygdx.game.Ray;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.tile.TileSet;
import com.mygdx.game.tile.TileType;

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
    private List<Float> raysX;
    private List<Float> raysY;
    private static double deg = 0.0174533;

    public RayCaster(double playerA, double playerX, double playerY, int gridSize , TileSet map) {
        this.playerA = playerA;
        this.playerX = playerX;
        this.playerY = playerY;
        this.map = map;
        this.gridSize = gridSize;
        float empty[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        this.raysX = new ArrayList(Arrays.asList(empty));
        this.raysY = new ArrayList(Arrays.asList(empty));

    }

    public void updatePlayerLoc(double playerA, double playerX, double playerY) {
        this.playerA = playerA;
        this.playerX = playerX;
        this.playerY = playerY;
    }

    public void createRayRenderers(int numRays) {
        this.lines = new ArrayList<>();
        for (int y = 0; y < numRays; y++) {
            lines.add(new ShapeRenderer());
        }
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

        //ray angles
        double rayA = this.playerA - 30 * deg;
        if (rayA < 0) {
            rayA += 2 * Math.PI;
        } else if (rayA > 2 * Math.PI) {
            rayA -= 2 * Math.PI;
        }
        for (int r = 0; r <= numRays; r++) {
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
//                        System.out.print("mapX: ");
//                        System.out.print(mapX);
//                        System.out.print(" RayX: ");
//                        System.out.print(rayXHor);
//                        System.out.print(" mapY: ");
//                        System.out.print(mapY);
//                        System.out.print(" RayY: ");
//                        System.out.println(rayYHor);
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
            } else if (rayA > Math.PI / 2 && rayA < 3 * Math.PI / 2) { //looking down
                rayXVer = (int) (playerX / gridSize) * gridSize - 0.0001;
                rayYVer = (playerX - rayXVer) * tanB + playerY;
                offsetX = -1 * gridSize;
                offsetY = -offsetX * tanB;
            } else if (rayA < Math.PI / 2 || rayA > 3 * Math.PI / 2) { //looking up
                rayXVer = (int) (playerX / gridSize) * gridSize + gridSize;
                rayYVer = (playerX - rayXVer) * tanB + playerY;
                offsetX = gridSize;
                offsetY = -offsetX * tanB;
            }
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
            rayA++;
            if (rayA < 0) {
                rayA += 2 * Math.PI;
            } else if (rayA > 2 * Math.PI) {
                rayA -= 2 * Math.PI;
            }
        }
    }
    private double dist(double ax, double ay, double bx, double by ) {
        return Math.sqrt((bx - ax) * (bx - ax) + (by - ay) * (by - ay));
    }

    private void setOptimalRay(double xVer,double yVer,double xHor,double yHor,boolean hitH, boolean hitV) {
        if (!hitH) {
            this.raysX.add((float)xVer);
            this.raysY.add((float)yVer);
        } else if (!hitV) {
            this.raysX.add((float)xHor);
            this.raysY.add((float)yHor);
        } else {
            if (dist(playerX, playerY, xVer, xVer) < dist(playerX, playerY, xHor, yHor)) {
                this.raysX.add((float)xVer);
                this.raysY.add((float)yVer);
            } else {
                List<Float> best = new ArrayList<>();
                this.raysX.add((float)xHor);
                this.raysY.add((float)yHor);
            }
        }
    }

    public void drawRays() {
        int rayNum = 0;
        for (ShapeRenderer line: lines) {
//            System.out.println(raysX.toString());
            line.begin(ShapeRenderer.ShapeType.Line);
            line.setColor(1,0,0,1);
            line.line((float)playerX, (float)playerY, raysX.get(rayNum), raysY.get(rayNum));
            line.end();
            rayNum++;
        }
    }
}
