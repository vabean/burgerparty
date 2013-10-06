package com.agateau.burgerparty.utils;

import java.util.HashMap;
import java.util.Map;

import com.agateau.burgerparty.utils.AnchorGroup.Rule;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.XmlReader;

public class UiBuilder {
	public UiBuilder(TextureAtlas atlas, Skin skin) {
		mAtlas = atlas;
		mSkin = skin;
	}

	public void build(XmlReader.Element parentElement, Group parentActor) {
		for (int idx=0, size = parentElement.getChildCount(); idx < size; ++idx) {
			XmlReader.Element element = parentElement.getChild(idx);
			String name = element.getName();
			Gdx.app.log("UiBuilder.build", "Parsing " + element);
			Actor actor = null;
			if (name.equals("Image")) {
				actor = createImage(element);
			} else if (name.equals("ImageButton")) {
				actor = createImageButton(element);
			} else if (name.equals("Group")) {
				actor = createGroup(element);
			} else if (name.equals("AnchorGroup")) {
				actor = createAnchorGroup(element);
			} else {
				throw new RuntimeException("Unknown UI element type: " + name);
			}
			assert(actor != null);
			applyActorProperties(actor, element, parentActor);
			String id = element.getAttribute("id", null);
			if (id != null) {
				if (mActorForId.containsKey(id)) {
					throw new RuntimeException("Duplicate ids: " + id);
				}
				mActorForId.put(id, actor);
			}
			Gdx.app.log("UiBuilder.build", "Actor: " + actor);
			if (actor instanceof Group) {
				build(element, (Group)actor);
			}
		}
	}

	public Actor getActor(String id) {
		Actor actor = mActorForId.get(id);
		assert(actor != null);
		return actor;
	}

	protected Image createImage(XmlReader.Element element) {
		Image image = new Image();
		String attr = element.getAttribute("name", "");
		if (!attr.isEmpty()) {
			TextureRegion region = mAtlas.findRegion(attr);
			image.setDrawable(new TextureRegionDrawable(region));
			if (image.getWidth() == 0) {
				image.setWidth(region.getRegionWidth());
			}
			if (image.getHeight() == 0) {
				image.setHeight(region.getRegionHeight());
			}
		}
		return image;
	}

	protected ImageButton createImageButton(XmlReader.Element element) {
		String styleName = element.getAttribute("style", "");
		ImageButton button = new ImageButton(mSkin, styleName);
		String imageName = element.getAttribute("imageName", "");
		if (!imageName.isEmpty()) {
			Drawable drawable = mSkin.getDrawable(imageName);
			button.getImage().setDrawable(drawable);
		}
		String imageColor = element.getAttribute("imageColor", "");
		if (!imageColor.isEmpty()) {
			Color color = Color.valueOf(imageColor);
			button.getImage().setColor(color);
		}
		return button;
	}

	protected Group createGroup(XmlReader.Element element) {
		return new Group();
	}

	protected AnchorGroup createAnchorGroup(XmlReader.Element element) {
		return new AnchorGroup();
	}

	protected void applyActorProperties(Actor actor, XmlReader.Element element, Group parentActor) {
		parentActor.addActor(actor);
		AnchorGroup anchorGroup = null;
		if (parentActor instanceof AnchorGroup) {
			anchorGroup = (AnchorGroup)parentActor;
		}
		String attr = element.getAttribute("x", "");
		if (!attr.isEmpty()) {
			actor.setX(Float.parseFloat(attr));
		}
		attr = element.getAttribute("y", "");
		if (!attr.isEmpty()) {
			actor.setY(Float.parseFloat(attr));
		}
		attr = element.getAttribute("width", "");
		if (!attr.isEmpty()) {
			actor.setWidth(Float.parseFloat(attr));
		}
		attr = element.getAttribute("height", "");
		if (!attr.isEmpty()) {
			actor.setHeight(Float.parseFloat(attr));
		}
		for (int idx = 0, size = ANCHOR_NAMES.length; idx < size; ++idx) {
			String anchorName = ANCHOR_NAMES[idx];
			attr = element.getAttribute(anchorName, "");
			if (!attr.isEmpty()) {
				assert(anchorGroup != null);
				Rule rule = parseRule(attr);
				rule.target = actor;
				rule.targetAnchor = ANCHORS[idx];
				anchorGroup.addRule(rule);
			}
		}
	}

	/**
	 * Parse a string of the form "$actorId $anchorName [$xOffset $yOffset]"
	 * @param txt
	 * @return
	 */
	private Rule parseRule(String txt) {
		Rule rule = new AnchorGroup.Rule();
		String[] tokens = txt.split(" +");
		assert(tokens.length == 1 || tokens.length == 3);
		String[] tokens2 = tokens[0].split("\\.");
		rule.reference = getActor(tokens2[0]);
		for (int idx = 0, size = ANCHOR_NAMES.length; idx < size; ++idx) {
			if (tokens2[1].equals(ANCHOR_NAMES[idx])) {
				rule.referenceAnchor = ANCHORS[idx];
				break;
			}
		}
		if (rule.referenceAnchor == null) {
			throw new RuntimeException("Invalid anchor name: '" + tokens[1] + "'");
		}
		if (tokens.length == 3) {
			rule.hSpace = Float.parseFloat(tokens[1]);
			rule.vSpace = Float.parseFloat(tokens[2]);
		}
		return rule;
	}

	private Map<String, Actor> mActorForId = new HashMap<String, Actor>();
	private TextureAtlas mAtlas;
	private Skin mSkin;

	private static final String[] ANCHOR_NAMES = {
		"topLeft",
		"topCenter",
		"topRight",
		"centerLeft",
		"center",
		"centerRight",
		"bottomLeft",
		"bottomCenter",
		"bottomRight"
	};
	private static final Anchor[] ANCHORS = {
		Anchor.TOP_LEFT,
		Anchor.TOP_CENTER,
		Anchor.TOP_RIGHT,
		Anchor.CENTER_LEFT,
		Anchor.CENTER,
		Anchor.CENTER_RIGHT,
		Anchor.BOTTOM_LEFT,
		Anchor.BOTTOM_CENTER,
		Anchor.BOTTOM_RIGHT
	};
}