package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.Setter;

import static de.thb.crazyhorseracing.entity.LobbyState.GAME_OVER;
import static de.thb.crazyhorseracing.entity.LobbyState.PLAYING;

public class GameTask implements Runnable {
    @Getter
    private final Game game;
    @Setter
    @Getter
    private volatile boolean running = true;

    public GameTask(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        game.start();
        System.out.println("GameTask started.");
        long lastTime = System.nanoTime();
        while (running && !Thread.currentThread().isInterrupted()) {
            long now = System.nanoTime();
            long deltananos = now - lastTime;
            lastTime = now;

            tick(deltananos);
            sleep();
        }

        onStop();
    }

    private void tick(long delta_ns) {
        synchronized (game) {
            game.processStep(delta_ns / 1_000_000_000.0);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(10); // 100 ticks/sec
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        running = false;
    }

    private void onStop() {
        game.getLobby().setLobbyState(GAME_OVER);
        System.out.println("GameTask stopped.");
    }
}