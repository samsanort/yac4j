package com.samsanort.yac4j.process;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by samu on 4/20/17.
 */
public class CycleRunnerTest {

    private CycleRunner testSubject;

    @Test
    public void run_notKilled_staysAlive() throws Exception {

        // Given
        Cycle cycle = aStubbedCycle();
        testSubject = new CycleRunner(cycle);

        // When
        runInThread(testSubject);
        waitOneCycle(cycle);

        // Then
        assertThat(testSubject.getState(), is(not(CycleRunner.State.DEAD)));
    }

    @Test
    public void kill_isAlive_dies() throws Exception {

        // Given
        Cycle cycle = aStubbedCycle();
        testSubject = new CycleRunner(cycle);
        runInThread(testSubject);

        // When
        testSubject.kill();
        waitOneCycle(cycle);

        // Then
        assertThat(testSubject.getState(), is(CycleRunner.State.DEAD));
    }

    private Cycle aStubbedCycle() {
        return new StubbedCycle();
    }

    private void runInThread(CycleRunner runner) {
        new Thread(runner).start();
    }

    private void waitOneCycle(Cycle cycle) throws Exception {
        Thread.sleep(cycle.getDelay());
    }

    private class StubbedCycle implements Cycle {

        @Override
        public void runCycle() {
        }

        @Override
        public long getDelay() {
            return 250;
        }
    }

}
