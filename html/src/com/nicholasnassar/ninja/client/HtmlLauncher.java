package com.nicholasnassar.ninja.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.nicholasnassar.ninja.NinjaGame;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        return new GwtApplicationConfiguration(960, 640);
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new NinjaGame(new HtmlPlatformFeatures());
    }
}