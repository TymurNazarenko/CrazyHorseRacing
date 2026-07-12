package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.Setter;

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
        System.out.printf("Game started on map %d with %d horses%n", game.map.id(), game.horses.size());
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
        System.out.printf("Game ended with winner %s%n", game.getWinner().getNickname());
    }
}