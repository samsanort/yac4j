package com.samsanort.yac4j.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by samu on 2/26/17.
 */
public class CycleRunner implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CycleRunner.class);

    private Cycle cycle;

    private boolean needed_ = true;
    private State state_;

    public CycleRunner(Cycle cycle) {

        this.cycle = cycle;
        setState(State.WAITING);
    }

    @Override
    public void run() {

        try {

            while (isNeeded()) {

                setState(State.WORKING);

                this.cycle.runCycle();

                setState(State.WAITING);

                Thread.sleep(this.cycle.getDelay());
            }

        } catch (InterruptedException ie) {
            logger.debug("Runner interrupted by user.");

        } finally {
            setState(State.DEAD);
        }
    }


    /**
     *
     */
    public void kill() {

        logger.debug("Runner marked to die.");
        this.setNeeded(false);
    }

    /**
     * @return
     */
    public synchronized State getState() {
        return this.state_;
    }

    private synchronized void setState(State state) {
        this.state_ = state;
    }

    private synchronized boolean isNeeded() {
        return this.needed_;
    }

    private synchronized void setNeeded(boolean value) {
        this.needed_ = value;
    }

    public enum State {WAITING, WORKING, DEAD}
}
