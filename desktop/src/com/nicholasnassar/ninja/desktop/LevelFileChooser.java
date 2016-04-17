package com.nicholasnassar.ninja.desktop;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class LevelFileChooser extends JFileChooser {
    public LevelFileChooser() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Levels", "lvl");

        setFileFilter(filter);
    }

    @Override
    protected JDialog createDialog(Component parent) {
        JDialog dialog = super.createDialog(parent);

        dialog.setAlwaysOnTop(true);

        return dialog;
    }
}
