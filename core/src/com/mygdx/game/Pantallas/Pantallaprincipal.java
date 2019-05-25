package com.mygdx.game.Pantallas;


import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.SpaceGame;
import com.mygdx.game.Clases.Disparo;
import com.mygdx.game.Clases.Explosion;
import com.mygdx.game.Clases.Naveenemiga;
import com.mygdx.game.tools.Collisiones;



public class Pantallaprincipal implements Screen  {

	public static final float SPEED = 300;

	public static final float SHIP_ANIMATION_SPEED = 0.5f;
	public static final int SHIP_WIDTH_PIXEL = 17;
	public static final int SHIP_HEIGHT_PIXEL = 32;
	public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
	public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;

	public static final float ROLL_TIMER_SWITCH_TIME = 0.25f;
	public static final float SHOOT_WAIT_TIME = 0.3f;

	public static final float MIN_ASTEROID_SPAWN_TIME = 0.05f;
	public static final float MAX_ASTEROID_SPAWN_TIME = 0.1f;

	Animation[] rolls;
	float delta;
	private Texture img2;

	float x;
	float y;
	int roll;
	float rollTimer;
	float stateTime;
	float shootTimer;
	float asteroidSpawnTimer;
	private int vida=0;
	Random random;

	SpaceGame game;

	ArrayList<Disparo> disparos;
	ArrayList<Naveenemiga> naveenemigas;
	ArrayList<Explosion> explosions;

	Texture blank;
	Texture controls;

	BitmapFont scoreFont;

	Collisiones playerRect;

	float health = 1;//0 = muerte, 1 = vida completa

	int score;
	private Sprite Nau;
	boolean showControls = true;
	private int maxbullets = 4;
	private int bulletast = 0;

	public Pantallaprincipal(SpaceGame game) {
		this.game = game;
		y = 15;
		x = SpaceGame.WIDTH / 2 - SHIP_WIDTH / 2;
		disparos = new ArrayList<Disparo>();
		naveenemigas = new ArrayList<Naveenemiga>();
		explosions = new ArrayList<Explosion>();
		scoreFont = new BitmapFont(Gdx.files.internal("fonts/score.fnt"));

		playerRect = new Collisiones(0, 0, SHIP_WIDTH, SHIP_HEIGHT);

		blank = new Texture("tret.png");


		score = 0;

		random = new Random();
		asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;

		shootTimer = 0;

		roll = 2;
		rollTimer = 0;
		rolls = new Animation[5];


	/*	Nau = new Sprite(img2, 0, 0, img2.getWidth(), img2.getHeight());
		Nau.setX((SCREEN_WIDTH - Nau.getWidth()) / 2);
		Nau.setY((SCREEN_HEIGHT - Nau.getHeight()) /10);*/

		TextureRegion[][] rollSpriteSheet = TextureRegion.split(new Texture("ship.png"), SHIP_WIDTH_PIXEL, SHIP_HEIGHT_PIXEL);

		rolls[0] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[2]);//All left
		rolls[1] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[1]);
		rolls[2] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[0]);//No tilt
		rolls[3] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[3]);
		rolls[4] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[4]);//Right


		game.barradevida.setSpeedFixed(false);




	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {



		RespawnNavesenemigas(delta);
		Dispara(delta);
		moura();
		//Actualizamos las  naveenemigas
		ArrayList<Naveenemiga> asteroidsToRemove = new ArrayList<Naveenemiga>();
		for (Naveenemiga naveenemiga : naveenemigas) {
			naveenemiga.update(delta);
			if (naveenemiga.remove) {
				asteroidsToRemove.add(naveenemiga);
			}
		}

		//Actualizamos las disparos
		ArrayList<Disparo> bulletsToRemove = new ArrayList<Disparo>();
		for (Disparo disparo : disparos) {
			disparo.update(delta);
			if (disparo.remove)
				bulletsToRemove.add(disparo);
		}

		//Actualizamos las explosiones
		ArrayList<Explosion> explosionsToRemove = new ArrayList<Explosion>();
		for (Explosion explosion : explosions) {
			explosion.update(delta);
			if (explosion.remove)
				explosionsToRemove.add(explosion);
		}
		explosions.removeAll(explosionsToRemove);



		//Despues que el usuario mueva , actualizamos para las collisiones
		playerRect.move(x, y);

		//Comprueba las coliciones despues de actualizar
		for (Disparo disparo : disparos) {
			for (Naveenemiga naveenemiga : naveenemigas) {
				if (disparo.getCollisionRect().collidesWith(naveenemiga.getCollisionRect())) {//Collision occured
					bulletsToRemove.add(disparo);
					if (maxbullets > bulletast) {

						asteroidsToRemove.add(naveenemiga);
						explosions.add(new Explosion(naveenemiga.getX(), naveenemiga.getY()));
						score += 100;
					} else {
						bulletast++;
					}


				}
			}
		}

		disparos.removeAll(bulletsToRemove);

		for (Naveenemiga naveenemiga : naveenemigas) {
			if (naveenemiga.getCollisionRect().collidesWith(playerRect)) {
				asteroidsToRemove.add(naveenemiga);
				health -= 0.1;

				//Si la barra de salud baja 3 veces ira a la pantalla de game over
				if (health <= 0) {
					vida++;
					if(vida<3){
						health=1;
					}

					else {
						this.dispose();
						game.setScreen(new Pantallagameover(game, score));
						return;
					}
				}
			}
		}
		naveenemigas.removeAll(asteroidsToRemove);

		stateTime += delta;

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.begin();

		game.barradevida.updateAndRender(delta, game.batch);

		GlyphLayout scoreLayout = new GlyphLayout(scoreFont, "" + score);
		scoreFont.draw(game.batch, scoreLayout, SpaceGame.WIDTH / 2 - scoreLayout.width / 2, SpaceGame.HEIGHT - scoreLayout.height - 10);


		//Dibujamos los disparos,naveenemigas y explosiones
		for (Disparo disparo : disparos) {
			disparo.render(game.batch);
		}

		for (Naveenemiga naveenemiga : naveenemigas) {
			naveenemiga.render(game.batch);

		}

		for (Explosion explosion : explosions) {
			explosion.render(game.batch);
		}


		//Dibujamos la salut e indicamos los colores que queremos
		if (health > 0.6f){
			game.batch.setColor(Color.GREEN);
		}

		else if (health > 0.2f){

			game.batch.setColor(Color.ORANGE);
		}
		else {
			game.batch.setColor(Color.RED);
		}
		game.batch.draw(blank, 0, 0, SpaceGame.WIDTH * health, 5);
		game.batch.setColor(Color.WHITE);


        game.batch.draw((TextureRegion) rolls[roll].getKeyFrame(stateTime, true), x, y, SHIP_WIDTH, SHIP_HEIGHT);
		game.batch.end();
	}

	//Metodos para mover la nave de derecha a izquierda y de izquerda a derecha
	private boolean isRight() {
		return Gdx.input.isKeyPressed(Keys.RIGHT) || (Gdx.input.isTouched() && game.cam.getInputInGameWorld().x >= SpaceGame.WIDTH / 2);
	}

	private boolean isLeft() {
		return Gdx.input.isKeyPressed(Keys.LEFT) || (Gdx.input.isTouched() && game.cam.getInputInGameWorld().x < SpaceGame.WIDTH / 2);
	}

	private boolean isJustRight() {
		return Gdx.input.isKeyJustPressed(Keys.RIGHT) || (Gdx.input.justTouched() && game.cam.getInputInGameWorld().x >= SpaceGame.WIDTH / 2);
	}

	private boolean isJustLeft() {
		return Gdx.input.isKeyJustPressed(Keys.LEFT) || (Gdx.input.justTouched() && game.cam.getInputInGameWorld().x < SpaceGame.WIDTH / 2);
	}


	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}

	//Metodo para el respawn de las naves , solo aparecen maximo 7
private void RespawnNavesenemigas(float delta){
	asteroidSpawnTimer -= delta;
	if (asteroidSpawnTimer <= 0) {
		asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;
		if (naveenemigas.size() <= 7) {

			naveenemigas.add(new Naveenemiga(random.nextInt(SpaceGame.WIDTH - Naveenemiga.WIDTH)));
		}

	}
}
//Metodo para que la nave dispare
private void Dispara(float delta){

	shootTimer += delta;
	if ((isRight() || isLeft()) && shootTimer >= SHOOT_WAIT_TIME) {
		shootTimer = 0;

		showControls = false;

		int offset = 4;
		if (roll == 1 || roll == 3) {//Nave un poco inclinada
            offset = 8;
        }
		if (roll == 0 || roll == 4) {//Totalmente inclinada
            offset = 16;
        }
		//La nave tiene dos disparos
		disparos.add(new Disparo(x + offset));
		disparos.add(new Disparo(x + SHIP_WIDTH - offset));
	}

}
//Metodo para mover la nave

private void moura(){
	if (isLeft()) {//Left
		x -= SPEED * Gdx.graphics.getDeltaTime();

		if (x < 0)
			x = 0;

		//Actualizamos cuando se ha clickeado
		if (isJustLeft() && !isRight() && roll > 0) {
			rollTimer = 0;
			roll--;
		}

		//Actualizamos
		rollTimer -= Gdx.graphics.getDeltaTime();
		if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME && roll > 0) {
			rollTimer -= ROLL_TIMER_SWITCH_TIME;
			roll--;
		}
	} else {
		if (roll < 2) {
			//Update roll to make it go back to center
			rollTimer += Gdx.graphics.getDeltaTime();
			if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME && roll < 4) {
				rollTimer -= ROLL_TIMER_SWITCH_TIME;
				roll++;
			}
		}
	}

	if (isRight()) {//Right
		x += SPEED * Gdx.graphics.getDeltaTime();

		if (x + SHIP_WIDTH > SpaceGame.WIDTH)
			x = SpaceGame.WIDTH - SHIP_WIDTH;

        //Actualizamos cuando se ha clickeado
		if (isJustRight() && !isLeft() && roll > 0) {
			rollTimer = 0;
			roll--;
		}

		//Actualizamos roll
		rollTimer += Gdx.graphics.getDeltaTime();
		if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME && roll < 4) {
			rollTimer -= ROLL_TIMER_SWITCH_TIME;
			roll++;
		}
	} else {
		if (roll > 2) {
			rollTimer -= Gdx.graphics.getDeltaTime();
			if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME && roll > 0) {
				rollTimer -= ROLL_TIMER_SWITCH_TIME;
				roll--;
			}
		}
	}
}

}



