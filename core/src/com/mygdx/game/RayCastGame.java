package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.utility.UserSettings;
import com.mygdx.game.screen.GameScreen;

public class RayCastGame extends Game {
	SpriteBatch batch;
	Texture img;
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;

	@Override
	public void create () {
		UserSettings settings = new UserSettings();
		settings.applySettings();
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, settings.getWidth(), settings.getHeight());
		this.setScreen(ScreenType.GAME_SCREEN);
	}

	public void setScreen(ScreenType screenType) {
		Screen currentScreen = getScreen();
		if (currentScreen != null) {
			currentScreen.dispose();
		}
		switch (screenType) {
			case GAME_SCREEN:
				setScreen(new GameScreen(this, this.camera));
				break;
			default:
				setScreen(new GameScreen(this, this.camera));
				break;
		}
	}

	public enum ScreenType {
		GAME_SCREEN
	}
}
