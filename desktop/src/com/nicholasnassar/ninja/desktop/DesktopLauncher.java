package com.nicholasnassar.ninja.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.nicholasnassar.ninja.NinjaGame;

import javax.swing.*;

public class DesktopLauncher {
    public static void main(String[] arg) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        new LwjglApplication(new NinjaGame(new DesktopPlatformFeatures()), config);
    }
}
