package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.Ray.RayCaster;
import com.mygdx.game.RayCastGame;
import com.mygdx.game.tile.Tile;
import com.mygdx.game.tile.TileSet;
import com.mygdx.game.tile.TileType;


import java.util.ArrayList;
import java.util.List;

import static com.mygdx.game.tile.TileType.*;

public class GameScreen extends ScreenAdapter {
    private final RayCastGame game;
    private OrthographicCamera camera;
    private ShapeRenderer player;
    private ShapeRenderer playerFace;
    private TileSet tileMap;
    private List<ShapeRenderer> walls3D;
    private List<List<ShapeRenderer>> walls2D;
    private int wallSize;
    private Float playerX, playerY, playerDx, playerDy, playerA;
    private static float PI = 3.1415926535f;
    private RayCaster rayCaster;

    public GameScreen(RayCastGame game, OrthographicCamera camera) {
        this.game = game;
        this.camera = camera;
        this.tileMap = new TileSet(10,10);
        this.createShapes();
        this.createplayer();
        this.rayCaster = new RayCaster(playerX, playerY, playerA, wallSize, tileMap);
        this.rayCaster.createRayRenderers(1);
    }
    private void createplayer() {
        this.player = new ShapeRenderer();
        this.playerFace = new ShapeRenderer();
        this.playerX = wallSize * 3f;
        this.playerY = wallSize * 3f;
        this.playerA= 0.3f;
        this.playerDx = (float)Math.cos(playerA) * 5;
        this.playerDy = (float)Math.cos(playerA) * 5;
    }

    private void createShapes() {
        this.wallSize = 64;
        this.walls3D = this.create3DWalls(30);
        this.walls2D = this.create2DWalls();
    }

    private List<List<ShapeRenderer>> create2DWalls() {
        int width = tileMap.getWidth();
        int height = tileMap.getHeight();
        List<List<ShapeRenderer>> walls2D = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            walls2D.add(new ArrayList<ShapeRenderer>());
            for (int x = 0; x < width; x++) {
                walls2D.get(y).add(new ShapeRenderer());
            }
        }
        return walls2D;
    }

    private List<ShapeRenderer> create3DWalls(int walls) {
        List<ShapeRenderer> walls3D = new ArrayList<ShapeRenderer>();
        for (int y = 0; y < walls; y++) {
            walls3D.add(new ShapeRenderer());
        }
        return walls3D;
    }

    private void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {

            this.playerX += playerDx;
            this.playerY += playerDy;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            this.playerX -= playerDx;
            this.playerY -= playerDy;

        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerA += 0.1f;
            if (playerA > 2 * Math.PI) {
                playerA -= 2 * (float)Math.PI;
            }
            playerDx = (float)Math.cos(playerA)*5;
            playerDy = (float)Math.sin(playerA)*5;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerA -= 0.1f;
            if (playerA < 0) {
              playerA += 2 * (float)Math.PI;
            }
            playerDx = (float)Math.cos(playerA)*5;
            playerDy = (float)Math.sin(playerA)*5;
        }
        this.rayCaster.updatePlayerLoc(playerA, playerX, playerY);
        this.rayCaster.createRays(1);
    }

    @Override
    public void render(float delta) {
        this.update();
//        System.out.println(String.format("x: %f  y: %f A: %f", playerX, playerY, playerA));
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.rayCaster.drawRays();
        this.render2DWalls();
        this.renderPlayer();
    }

    private void render2DWalls() {
        int width = tileMap.getWidth();
        int height = tileMap.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float xLoc = x * this.wallSize;
                float yLoc = y * this.wallSize;
                Tile block = tileMap.tileAtXY(x,y);
                ShapeRenderer wall = walls2D.get(y).get(x);
                if (block.getTileType() == WALL) {
                    wall.begin(ShapeRenderer.ShapeType.Filled);
                    wall.setColor(1,1,1,0);

                } else {
                    wall.begin(ShapeRenderer.ShapeType.Line);
                    wall.setColor(0,0,0,0);
                }
                wall.rect(xLoc, yLoc, wallSize, wallSize);
                wall.end();
            }
        }
    }

    private void renderPlayer() {
        this.player.begin(ShapeRenderer.ShapeType.Filled);
        this.player.circle(playerX, playerY, 3);
        this.player.end();
        this.playerFace.begin(ShapeRenderer.ShapeType.Line);
        this.playerFace.line(playerX, playerY, (playerX + playerDx * 3), (playerY + playerDy * 3));
        this.playerFace.setColor(Color.YELLOW);
        this.playerFace.end();
    }
}
