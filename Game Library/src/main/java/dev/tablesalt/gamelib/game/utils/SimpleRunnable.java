package dev.tablesalt.gamelib.game.utils;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.plugin.SimplePlugin;

public abstract class SimpleRunnable implements Runnable{
    /**
     * Runs the runnable until canceled
     * <p>
     * off by default.
     */
    private final int INFINITE = -1;

    /**
     * How long to wait before starting this countdown (in ticks)?
     * <p>
     * Set to 1 second.
     */
    private final int START_DELAY;

    /**
     * How long to wait before ticking the next count (in ticks)?
     * <p>
     * Set to 1 second.
     */
    private  final int TICK_PERIOD;

    /**
     * The time in seconds we are counting down from
     */
    @Getter
    private final int countdownSeconds;

    /**
     * How many seconds have passed since the start ?
     */
    @Getter(AccessLevel.PROTECTED)
    private int secondsSinceStart = 0;

    /**
     * The internal task from Bukkit associated with this countdown
     */
    private int taskId = -1;

    /**
     * Create new countdown from the given time
     *
     * @param time
     */
    protected SimpleRunnable(final SimpleTime time) {
        this((int) time.getTimeSeconds(),0,20);
    }

    /**
     * Create new countdown
     *
     * @param countdownSeconds
     */
    protected SimpleRunnable(final int countdownSeconds, int startDelay, int tickPeriod) {
        this.countdownSeconds = countdownSeconds;
        this.START_DELAY = startDelay;
        this.TICK_PERIOD = tickPeriod;
    }

    @Override
    public final void run() {
        this.secondsSinceStart++;

        if (this.secondsSinceStart < this.countdownSeconds || countdownSeconds == -1)
            try {
                this.onTick();

            } catch (final Throwable t) {
                try {
                    this.onTickError(t);
                } catch (final Throwable tt) {
                    Common.log("Unable to handle onTickError, got " + t + ": " + tt.getMessage());
                }

                Common.error(t,
                        "Error in countdown!",
                        "Seconds since start: " + this.secondsSinceStart,
                        "Counting till: " + this.countdownSeconds,
                        "%error");
            }
        else {
            this.cancel();
        }
    }

    /**
     * Called when this countdown is launched
     */
    protected void onStart() {
    }

    /**
     * Called on each tick (by default each second) till we count down to 0
     */
    protected abstract void onTick();

    /**
     * Called when the clock hits the final 0 and stops.
     */
    protected abstract void onEnd();

    /**
     * Called when the {@link #onTick()} method throws an error (we already log the error)
     *
     * @param t
     */
    protected void onTickError(final Throwable t) {
    }

    /**
     * Return the time left in seconds
     *
     * @return
     */
    public int getTimeLeft() {
        return this.countdownSeconds - this.secondsSinceStart;
    }

    /**
     * Starts this countdown failing if it is already running
     */
    public final void launch() {
        Valid.checkBoolean(!this.isRunning(), "Task " + this + " already scheduled!");

        final BukkitTask task = Bukkit.getScheduler().runTaskTimer(SimplePlugin.getInstance(), this, START_DELAY, TICK_PERIOD);
        this.taskId = task.getTaskId();

        this.onStart();
    }

    /**
     * Cancels this countdown, failing if it is not scheduled (use {@link #isRunning()})
     */
    public void cancel() {
        Bukkit.getScheduler().cancelTask(this.getTaskId());

        this.taskId = -1;
        this.secondsSinceStart = 0;
    }

    public final void end() {
        cancel();
        onEnd();
    }

    /**
     * Return true if this countdown is running
     *
     * @return
     */
    public final boolean isRunning() {
        return this.taskId != -1;
    }

    /**
     * Return the bukkit task or fails if not running
     *
     * @return
     */
    public final int getTaskId() {
        Valid.checkBoolean(this.isRunning(), "Task " + this + " not scheduled yet");

        return this.taskId;
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + "{" + this.countdownSeconds + ", id=" + this.taskId + "}";
    }
}
