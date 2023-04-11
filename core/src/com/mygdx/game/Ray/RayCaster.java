package com.mygdx.game.Ray;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.tile.TileSet;
import com.mygdx.game.tile.TileType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mygdx.game.Ray.Direction.*;

public class RayCaster {
    private double playerA;
    private double playerX;
    private double playerY;
    private TileSet map;
    private int gridSize;
    private List<ShapeRenderer> lines;
    private List<Float> raysX;
    private List<Float> raysY;
    private List<Float> raysXDebug;
    private List<Float> raysYDebug;
    private static double deg = 0.0174533;
    private int debugRayX;
    private int debugRayY;


    public RayCaster(double playerA, double playerX, double playerY, int gridSize , TileSet map) {
        this.playerA = playerA;
        this.playerX = playerX;
        this.playerY = playerY;
        this.debugRayX = 0;
        this.debugRayY = 0;
        this.map = map;
        this.gridSize = gridSize;
        float empty[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        this.raysX = new ArrayList(Arrays.asList(empty));
        this.raysY = new ArrayList(Arrays.asList(empty));
        this.raysXDebug = new ArrayList(Arrays.asList(empty));
        this.raysYDebug = new ArrayList(Arrays.asList(empty));

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
        this.raysX.clear();
        this.raysY.clear();

        // Ray Stuff
        double theta = this.playerA;
        Direction direction;
        if (theta > 0 && theta < Math.PI / 2) { // up right
            theta = (Math.PI / 2) -  theta;
            direction = UR;
        } else if (theta >= Math.PI / 2 && theta < Math.PI) { // up left
            theta = theta - (Math.PI / 2);
            direction = UL;
        } else if (theta > Math.PI && theta < (3 * Math.PI) / 2) { // down left
            theta = ((3 * Math.PI) / 2) - theta;
            direction = DL;
        } else if (theta >= (3 * Math.PI) / 2 && theta < 2 * Math.PI) { // down right
            theta = theta - ((3 * Math.PI) / 2);
            direction = DR;
        } else {
            direction = UR;
            System.out.println("side On");
        }
        // Horizontal Checks
        int dof = 2;
        double ryOffset = this.playerY % this.gridSize;
        double rxOffset;
        for (int i = 0; i < dof; i++) {
            double ry;
            double rx;
            int xIndex;
            int yIndex;
            if (direction == UR) {
                ryOffset = this.gridSize - ryOffset + (this.gridSize * i);
                rxOffset = ryOffset * Math.tan(theta);
                ry = playerY + ryOffset;
                rx = playerX + rxOffset;
                xIndex = (int)Math.floor(rx/ this.gridSize);
                yIndex = (int)Math.ceil(ry/this.gridSize);
            } else if (direction == UL) {
                ryOffset = this.gridSize - ryOffset + (this.gridSize * i);
                rxOffset = ryOffset * Math.tan(theta);
                ry = playerY + ryOffset;
                rx = playerX - rxOffset;
                xIndex = (int)Math.floor(rx/ this.gridSize);
                yIndex = (int)Math.ceil(ry/this.gridSize);
            } else if (direction == DL) {
                ryOffset = ryOffset + (this.gridSize * (i));
                rxOffset = ryOffset * Math.tan(theta);
                ry = playerY - ryOffset;
                rx = playerX - rxOffset;
                xIndex = (int)Math.floor(rx/ this.gridSize);
                yIndex = (int)Math.ceil(ry/this.gridSize);
            } else { // DR
                ryOffset = ryOffset + (this.gridSize * (i));
                rxOffset = ryOffset * Math.tan(theta);
                ry = playerY - ryOffset;
                rx = playerX + rxOffset;
                xIndex = (int)Math.floor(rx/ this.gridSize);
                yIndex = (int)Math.ceil(ry/this.gridSize);
            }

            TileType horizontal = TileType.EMPTY;
            try {
                horizontal = map.tileAtXY(xIndex, yIndex).getTileType();
            } catch (IndexOutOfBoundsException e) {

            }
            if (horizontal == TileType.WALL) {
                System.out.println((rxOffset /this.gridSize)  + " , "+ (ryOffset /this.gridSize) +" | " + xIndex + " , " + yIndex);
                this.raysX.add((float)rx);
                this.raysY.add((float)ry);
                break;
            }
            this.debugRayX = xIndex;
            this.debugRayY = yIndex;
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
        if (raysX.size() > 0) {
            for (ShapeRenderer line : lines) {
                line.begin(ShapeRenderer.ShapeType.Line);
                line.setColor(1, 0, 0, 1);
                line.line((float) playerX, (float) playerY, raysX.get(rayNum), raysY.get(rayNum));
                line.end();
                rayNum++;
                break;
            }
        } else {
            System.out.println("(" + this.debugRayX + " , " + this.debugRayY + ")");
        }
    }
}


//        if (rayA > 0 && rayA < Math.PI / 2) { // up right
//            System.out.println("UR");
//            rayYHor = this.gridSize - (this.playerY % this.gridSize);
//            theta = (Math.PI / 2) -  rayA;
//            rayXHor = rayYHor * Math.tan(theta) * -1;
//            xIntercept = Math.floor((rayXHor - playerX % this.gridSize) / gridSize);
//        } else if (rayA >= Math.PI / 2 && rayA < Math.PI) { // up left
//            System.out.println("UL");
//            rayYHor = this.gridSize - (this.playerY % this.gridSize);
//            theta = rayA - (Math.PI / 2);
//            rayXHor = rayYHor * Math.tan(theta);
//            xIntercept = Math.floor((rayXHor - playerX % this.gridSize) / gridSize);
//        } else if (rayA > Math.PI && rayA < (3 * Math.PI) / 2) { // down left
//            System.out.println("DL");
//            rayYHor = (this.playerY % this.gridSize);
//            theta = ((3 * Math.PI) / 2) - rayA;
//            rayXHor = rayYHor * Math.tan(theta);
//            xIntercept = Math.floor((rayXHor - playerX % this.gridSize) / gridSize);
//        } else if (rayA >= (3 * Math.PI) / 2 && rayA < 2 * Math.PI) { // down right
//            System.out.println("DR");
//            rayYHor = (this.playerY % this.gridSize);
//            theta = rayA - ((3 * Math.PI) / 2);
//            rayXHor = rayYHor * Math.tan(theta) * -1;
//            xIntercept = Math.floor((rayXHor - playerX % this.gridSize) / gridSize);
//        } else {
//            System.out.println("side ons");
//        }
//
//        System.out.println(xIntercept);
//        int dof = 99;
//        for (int i = 0; i < dof; i++) {
//            if (rayXHor > 0) {
//                double rayGridY = rayYHor + this.gridSize;
//
//            } else {
//
//            }
////            int rayGridX = xIntercept;
////            int rayGridY = (int)(this.playerY / gridSize);
////            TileType horizontal = map.tileAtXY(rayGridX, rayGridY).getTileType();
////            TileType vertical = map.tileAtXY(rayGridX, rayGridY).getTileType();
////            if (horizontal == TileType.WALL);
////                break;
//        }

//        System.out.println("Cos: "  + String.valueOf(rayCos) + " Sin: "  + String.valueOf(raySin));

// Wall finder

//        if (rayA < 0) {
//            rayA += 2 * Math.PI;
//        } else if (rayA > 2 * Math.PI) {
//            rayA -= 2 * Math.PI;
//        }
//        for (int r = 0; r < numRays; r++) {
//            rayX = this.playerX;
//            rayY = this.playerY;
//
//            rayA++;
//            if (rayA < 0) {
//                rayA += 2 * Math.PI;
//            } else if (rayA > 2 * Math.PI) {
//                rayA -= 2 * Math.PI;
//            }
//        }
