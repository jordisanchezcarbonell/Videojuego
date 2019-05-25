package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Pantallas.Pantallaprincipal;
import com.mygdx.game.tools.Barradevida;
import com.mygdx.game.tools.GameCamera;


public class SpaceGame extends Game {
	
	public static final int WIDTH = 480;
	public static final int HEIGHT = 720;
	public static boolean IS_MOBILE = false;
	
	public SpriteBatch batch;
	public Barradevida barradevida;
	public GameCamera cam;
	
	@Override
	public void create () {

		batch = new SpriteBatch();
		cam = new GameCamera(WIDTH, HEIGHT);



		this.barradevida = new Barradevida();
		this.setScreen(new Pantallaprincipal(this));
	}

	@Override
	public void render () {
		batch.setProjectionMatrix(cam.combined());
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		cam.update(width, height);
		super.resize(width, height);
	}
	
}
