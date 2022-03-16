package com.mygdx.game.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.RayCastGame;

public class GameScreen extends ScreenAdapter {
    private final RayCastGame game;
    private OrthographicCamera camera;

    public GameScreen(RayCastGame game, OrthographicCamera camera) {
        this.game = game;
        this.camera = camera;
    }
}
