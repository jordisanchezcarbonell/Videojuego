package com.mygdx.game.Clases;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.SpaceGame;
import com.mygdx.game.tools.Collisiones;


public class Naveenemiga {
	
	public static final int SPEED = 150;
	public static final int WIDTH = 32;
	public static final int HEIGHT = 32;
	private static Texture texture;
	
	float x, y;

	Collisiones rect;
	public boolean remove = false;
	
	public Naveenemiga(float x) {
		this.x = x;

		this.y = SpaceGame.HEIGHT;
		this.rect = new Collisiones(x, y, WIDTH, HEIGHT);
		
		if (texture == null) {
            texture = new Texture("nauenemiga.png");
        }
	}
	
	public void update (float deltaTime) {
		y -= SPEED * deltaTime;
		if (y < -HEIGHT) {
            remove = true;


        }
		rect.move(x, y);
	}
	
	public void render (SpriteBatch batch) {
		batch.draw(texture, x, y, WIDTH, HEIGHT);
	}
	
	public Collisiones getCollisionRect () {
		return rect;
	}
	
	public float getX () {
		return x;
	}
	
	public float getY () {
		return y;
	}
	
}
