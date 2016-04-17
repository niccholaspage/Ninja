package com.nicholasnassar.ninja.desktop;

import com.badlogic.gdx.files.FileHandle;
import com.nicholasnassar.ninja.PlatformFeatures;
import com.nicholasnassar.ninja.systems.LevelEditorSystem;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DesktopPlatformFeatures implements PlatformFeatures {
    @Override
    public void loadLevel(final LevelEditorSystem system) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JFileChooser chooser = new LevelFileChooser();

                        int result = chooser.showOpenDialog(null);

                        if (result == JFileChooser.APPROVE_OPTION) {
                            system.load(new FileHandle(chooser.getSelectedFile()));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void saveLavel(final LevelEditorSystem system) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFileChooser chooser = new LevelFileChooser();

                int result = chooser.showSaveDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();

                    if (!file.getName().toLowerCase().endsWith(".lvl")) {
                        file = new File(file + ".lvl");
                    }

                    system.save(new FileHandle(file));
                }
            }
        });
    }
}
