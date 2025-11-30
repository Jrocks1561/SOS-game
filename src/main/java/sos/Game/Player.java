package sos.Game;

import java.awt.Color;

public interface Player {
    String name();
    Color color();

    static Player of(String n, Color c) {
        return new Player() {
            @Override
            public String name() { return n; }

            @Override
            public Color color() { return c; }
        };
    }

    static Player of(String n) {
        return of(n, Color.BLACK);
    }
}
