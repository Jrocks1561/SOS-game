package sos.game;

import java.awt.Color;

public interface Player {
    String name();
    Color color();

    // factory with explicit color
    static Player of(String n, Color c) {
        return new Player() {
            @Override
            public String name() { return n; }

            @Override
            public Color color() { return c; }
        };
    }

    // old convenience factory, default color if not specified
    static Player of(String n) {
        return of(n, Color.BLACK);
    }
}
