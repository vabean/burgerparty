package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.model.World;
import com.agateau.burgerparty.view.WorldView;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScreen implements Screen {
	private Stage mStage;
	private World mWorld;
	private WorldView mWorldView;

	public GameScreen(TextureAtlas atlas) {
		mStage = new Stage(0, 0, true);
		mWorld = new World();
		mWorldView = new WorldView(mWorld, atlas);
		mStage.addActor(mWorldView);
		Gdx.input.setInputProcessor(mStage);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		mStage.act(delta);
		mStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		mStage.setViewport(width, height, true);
	}

	@Override
	public void show() {
		mWorld.restart();
	}

	@Override
	public void hide() {
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
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
