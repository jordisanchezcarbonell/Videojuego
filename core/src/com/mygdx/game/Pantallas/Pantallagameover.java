package com.mygdx.game.Pantallas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.SpaceGame;
import com.mygdx.game.tools.Barradevida;



public class Pantallagameover implements Screen {

	private static final int BANNER_WIDTH = 350;
	private static final int BANNER_HEIGHT = 100;

	SpaceGame game;

	int score, highscore;

	Texture gameOverBanner;
	BitmapFont scoreFont;

	public Pantallagameover(SpaceGame game, int score) {
		this.game = game;
		this.score = score;
        //obtenemos el highscore del archivo guardado
		Preferences prefs = Gdx.app.getPreferences("spacegame");
		this.highscore = prefs.getInteger("highscore", 0);

		//Comprobamos si se ha superado la puntuacion
		if (score > highscore) {
			prefs.putInteger("highscore", score);
			prefs.flush();
		}

		//Cargamos las texturas ylas fuentes-
		gameOverBanner = new Texture("game_over.png");
		scoreFont = new BitmapFont(Gdx.files.internal("fonts/score.fnt"));

		game.barradevida.setSpeedFixed(true);
		game.barradevida.setSpeed(Barradevida.DEFAULT_SPEED);
	}

	@Override
	public void show () {}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.begin();

		game.barradevida.updateAndRender(delta, game.batch);

		game.batch.draw(gameOverBanner, SpaceGame.WIDTH / 2 - BANNER_WIDTH / 2, SpaceGame.HEIGHT - BANNER_HEIGHT - 15, BANNER_WIDTH, BANNER_HEIGHT);

		GlyphLayout scoreLayout = new GlyphLayout(scoreFont, "Score: \n" + score, Color.WHITE, 0, Align.left, false);
		GlyphLayout highscoreLayout = new GlyphLayout(scoreFont, "Highscore: \n" + highscore, Color.WHITE, 0, Align.left, false);
		scoreFont.draw(game.batch, scoreLayout, SpaceGame.WIDTH / 2 - scoreLayout.width / 2, SpaceGame.HEIGHT - BANNER_HEIGHT - 15 * 2);
		scoreFont.draw(game.batch, highscoreLayout, SpaceGame.WIDTH / 2 - highscoreLayout.width / 2, SpaceGame.HEIGHT - BANNER_HEIGHT - scoreLayout.height - 15 * 3);

		float touchX = game.cam.getInputInGameWorld().x, touchY = SpaceGame.HEIGHT - game.cam.getInputInGameWorld().y;

		GlyphLayout tryAgainLayout = new GlyphLayout(scoreFont, "Try Again");


		float tryAgainX = SpaceGame.WIDTH / 2 - tryAgainLayout.width /2;
		float tryAgainY = SpaceGame.HEIGHT / 2 - tryAgainLayout.height / 2;


		//Comprovamos si ha dado a try again
		if (touchX >= tryAgainX && touchX < tryAgainX + tryAgainLayout.width && touchY >= tryAgainY - tryAgainLayout.height && touchY < tryAgainY) {
            tryAgainLayout.setText(scoreFont, "Try Again", Color.YELLOW, 0, Align.left, false);
        }


		//Si se le ha clickeado volveremos a lanzar el jeugo
		if (Gdx.input.isTouched()) {
			if (touchX > tryAgainX && touchX < tryAgainX + tryAgainLayout.width && touchY > tryAgainY - tryAgainLayout.height && touchY < tryAgainY) {
				this.dispose();
				game.batch.end();
				game.setScreen(new Pantallaprincipal(game));
				return;
			}


		}

		//Dibujamos los botones
		scoreFont.draw(game.batch, tryAgainLayout, tryAgainX, tryAgainY);


		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
