package com.agateau.burgerparty.utils;

import java.io.IOException;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class CounterGameStat extends GameStat {
    private int mValue = 0;

    @Override
    public void reset() {
        mValue = 0;
        changed.emit();
    }

    @Override
    public void load(XmlReader.Element element) {
        mValue = element.getIntAttribute("value", 0);
    }

    @Override
    public void save(XmlWriter writer) throws IOException {
        writer.attribute("value", String.valueOf(mValue));
    }

    public void increase() {
        mValue++;
        changed.emit();
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        if (mValue != value) {
            mValue = value;
            changed.emit();
        }
    }
}
