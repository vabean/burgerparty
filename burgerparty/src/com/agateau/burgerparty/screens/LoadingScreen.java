package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;

public class LoadingScreen implements Screen {
	public Signal0 ready = new Signal0();

	public LoadingScreen(AssetManager assetManager) {
		super();
		mAssetManager = assetManager;
	}

	@Override
	public void render(float delta) {
		boolean done = mAssetManager.update();
		float progress = mAssetManager.getProgress();
		Gdx.app.log("LoadingScreen", "Loading progress:" + progress);
		float p = mAssetManager.getProgress();
		Gdx.gl.glClearColor(p, p, p, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (done) {
			ready.emit();
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
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

	private AssetManager mAssetManager;
}
