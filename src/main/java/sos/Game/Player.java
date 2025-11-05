package sos.game;

@FunctionalInterface
public interface Player {
    String name();

    // convenience factory so we don’t need a new class/file
    static Player of(String n) { return () -> n; }
}
