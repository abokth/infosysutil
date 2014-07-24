package se.kth.sys.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.junit.Test;

import se.kth.sys.util.ApplicationMonitor.Status;

public class ApplicationMonitorTest {

    @Test
    public void okCheckSummarizesAsOk() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor("foo");

        monitor.addCheck("Hello", new HappyTest());

        assertEquals("APPLICATION_STATUS: OK foo tests: 1 ok\n"
                + "Hello: OK Happy World\n", monitor.createMonitorReport());
    }

    @Test
    public void errorCheckSummarizesAsError() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor("foo");

        monitor.addCheck("Hello", new HappyTest());
        monitor.addCheck("Hello2", new FailingTest());

        assertEquals("APPLICATION_STATUS: ERROR foo tests: 1 failed, 1 ok\n"
                + "Hello: OK Happy World\n"
                + "Hello2: ERROR Cruel World\n",
                monitor.createMonitorReport());
    }

    @Test
    public void exceptionInTestIsError() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor("foo");

        monitor.addCheck("Hello", new Callable<Status>() {
            public Status call() throws Exception {
                throw new RuntimeException("Expected failure");
            }
        });

        assertEquals("APPLICATION_STATUS: ERROR foo tests: 1 failed, 0 ok\n"
                + "Hello: ERROR Exception executing test java.lang.RuntimeException: Expected failure\n",
                monitor.createMonitorReport());
    }

    @Test
    public void timeout() throws IOException {
        final int timeoutSec = 1;
        final long timeoutMs = timeoutSec * 1000;
        final long testMarginMs = 300;
        ApplicationMonitor monitor = new ApplicationMonitor("foo", timeoutSec);
        long startTs = System.currentTimeMillis();
        monitor.addCheck("Take", new Callable<Status>() {
            public Status call() throws Exception {
                Thread.sleep(timeoutMs * 2);
                return Status.OK("Time out"); }
        });
        monitor.addCheck("Hello", new HappyTest());
        String report = monitor.createMonitorReport();
        long elapsed = System.currentTimeMillis() - startTs;

        assertTrue("Elapsed should be approx 1 s, is " + elapsed,
                elapsed < timeoutMs + testMarginMs
                && elapsed > timeoutMs - testMarginMs);
        assertTrue(report, report.startsWith("APPLICATION_STATUS: ERROR "));
        assertTrue(report, report.contains("Take: ERROR "
                + "Timeout executing test after")); //ignore ms suffix
        assertTrue(report, report.contains("Hello: OK "
                + "Happy World")); //make sure other tests still pass completed
    }


    @Test
    public void duplicateCheckKeysNotAllowed() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor("foo");

        monitor.addCheck("Hello", new HappyTest());
        try {
            monitor.addCheck("Hello", new HappyTest());
            fail("Duplicate key should not be allowed");
        } catch (IllegalArgumentException e) {
            assertEquals("Implicit redefintion of existing key 'Hello' not allowed.", e.getMessage());
        }
    }

    @Test
    public void noChecksNotOkStatus() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor("foo");
        assertEquals("APPLICATION_STATUS: ERROR No checks configured\n",
                monitor.createMonitorReport());
    }

    private static final class HappyTest implements Callable<Status> {
        public Status call() throws Exception {
            return Status.OK("Happy %s", "World");
        }
    }

    private static final class FailingTest implements Callable<Status> {
        public Status call() throws Exception {
            return Status.ERROR("Cruel %s", "World");
        }
    }
}
