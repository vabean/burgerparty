package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.XmlReader;

public class NewWorldScreen extends BurgerPartyScreen {
	private static final float ZOOM_DURATION_INTERVAL = 1.2f;
	private int mWorldIndex;
	private float mDuration;
	private float mTime = 0;

	private float mDotInterval;
	private float mLastDotTime = 0;
	private TextureRegion mDotRegion;

	private Bezier<Vector2> mPath = new Bezier<Vector2>();
	private Vector2 mTmpV = new Vector2();
	private Image mPlane;
	private Image mBackground;

	public NewWorldScreen(BurgerPartyGame game, int worldIndex) {
		super(game);
		mWorldIndex = worldIndex;

		mDotRegion = getTextureAtlas().findRegion("newworld/dot");
		createBackground();
		createPlane();
		loadXml();
		createRefreshHelper();
	}

	@Override
	public void onBackPressed() {
		startNextLevel();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		boolean done = act(delta);
		if (done) {
			startNextLevel();
		}
	}

	private boolean act(float delta) {
		mTime += delta;
		if (mTime > mDuration) {
			mTime = mDuration;
			return true;
		}

		float scale = 1;
		if (mTime < ZOOM_DURATION_INTERVAL) {
			scale = mTime / ZOOM_DURATION_INTERVAL;
		} else if (mTime > mDuration - ZOOM_DURATION_INTERVAL) {
			scale = (mDuration - mTime) / ZOOM_DURATION_INTERVAL;
		}
		mPlane.setScale(1 - (float)Math.pow(scale - 1, 2));

		float oldX = mPlane.getX();
		float oldY = mPlane.getY();
		mPath.valueAt(mTmpV, mTime / mDuration);
		mPlane.setPosition(mTmpV.x - mPlane.getWidth() / 2, mTmpV.y - mPlane.getHeight() / 2);

		float angle = MathUtils.atan2(mPlane.getY() - oldY, mPlane.getX() - oldX);
		mPlane.setRotation(MathUtils.radiansToDegrees * angle);

		if (mTime - mLastDotTime > mDotInterval) {
			addDot(scale);
			mLastDotTime = mTime;
		}
		Camera camera = getStage().getCamera();
		float xBorder = getStage().getWidth() / 2;
		float yBorder = getStage().getHeight() / 2;
		camera.position.x = MathUtils.clamp(MathUtils.round(mPlane.getX()), xBorder, mBackground.getWidth() - xBorder);
		camera.position.y = MathUtils.clamp(MathUtils.round(mPlane.getY()), yBorder, mBackground.getHeight() - yBorder);
		return false;
	}

	private void addDot(float scale) {
		Image image = new Image(mDotRegion);
		mPath.valueAt(mTmpV, mTime / mDuration);
		image.setPosition(
				MathUtils.round(mTmpV.x - image.getWidth() / 2),
				MathUtils.round(mTmpV.y - image.getHeight() / 2)
				);
		image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
		image.setScale(scale);
		getStage().addActor(image);
		mPlane.toFront();
	}

	private void createBackground() {
		mBackground = new Image(getTextureAtlas().findRegion("levels/" + (mWorldIndex + 1) + "/newworld-map"));
		getStage().addActor(mBackground);

	}

	private void createPlane() {
		mPlane = new Image(getTextureAtlas().findRegion("newworld/plane"));
		mPlane.setOrigin(mPlane.getWidth() / 2, mPlane.getHeight() / 2);
		getStage().addActor(mPlane);
	}

	private void loadXml() {
		XmlReader.Element rootElement = FileUtils.parseXml(FileUtils.assets("levels/" + (mWorldIndex + 1) + "/newworld.xml"));
		mDuration = rootElement.getFloatAttribute("duration");
		mDotInterval = rootElement.getFloatAttribute("dotInterval");

		XmlReader.Element pointsElement = rootElement.getChildByName("points");
		Vector2[] points = new Vector2[pointsElement.getChildCount()];
		final float height = mBackground.getHeight();
		int idx = 0;
		for (XmlReader.Element pointElement: pointsElement.getChildrenByName("point")) {
			points[idx++] = new Vector2(pointElement.getFloatAttribute("x"), height - pointElement.getFloatAttribute("y"));
		}
		assert(points.length >= 2);
		mPath.set(points);
	}

	private void createRefreshHelper() {
		new RefreshHelper(getStage()) {
			@Override
			protected void refresh() {
				getGame().showNewWorldScreen(mWorldIndex);
			}
		};
	}

	private void startNextLevel() {
		getGame().startLevel(mWorldIndex, 0);
	}
}
