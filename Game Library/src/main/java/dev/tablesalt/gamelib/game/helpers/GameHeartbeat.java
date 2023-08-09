package dev.tablesalt.gamelib.game.helpers;
import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import org.mineacademy.fo.model.Countdown;
import org.mineacademy.fo.model.SimpleTime;

/**
 * The main heartbeat of the game, put anything you want
 * to be repeating/updating in the onTick method of this class
 */
public class GameHeartbeat extends Countdown {

    private final Game game;

    private final SimpleRunnable tickFastRunnable;

    public GameHeartbeat(Game game, SimpleTime timeUntilStop) {
        super(timeUntilStop);
        this.game = game;
        this.tickFastRunnable = new TickFastRunnable();

    }

    @Override
    protected void onStart() {
       tickFastRunnable.launch();
    }

    @Override
    protected void onTick() {

    }

    protected void onTickFast() {

    }

    @Override
    protected void onEnd() {
        cancelSubTasks();
    }

    @Override
    protected void onTickError(Throwable t) {
        cancelSubTasks();
    }

    public void stopIfBeating() {
        if (isRunning()) {
            cancel();
            cancelSubTasks();
        }

    }

    private void cancelSubTasks() {
        tickFastRunnable.cancel();
    }


    private class TickFastRunnable extends SimpleRunnable {
        protected TickFastRunnable() {
            super(-1,0,5);
        }

        @Override
        protected void onTick() {
            onTickFast();
        }

        @Override
        protected void onEnd() {

        }
    }
}
