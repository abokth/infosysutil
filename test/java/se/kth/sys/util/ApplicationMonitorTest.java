package se.kth.sys.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.junit.Test;

import se.kth.sys.util.ApplicationMonitor.Status;

public class ApplicationMonitorTest {

    @Test
    public void okCheckSummarizesAsOk() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor();

        monitor.addCheck("Hello", new Callable<Status>() { 
            public Status call() throws Exception { return fakeTest(Status.OK("World")); }
        });

        assertEquals("APPLICATION_STATUS: OK Every component is working\n" +
                "Hello: OK World\n", monitor.createMonitorReport());
    }

    @Test
    public void errorCheckSummarizesAsError() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor();

        monitor.addCheck("Hello", new Callable<Status>() { 
            public Status call() throws Exception { return fakeTest(Status.OK("Happy World")); }
        });
        monitor.addCheck("Hello2", new Callable<Status>() { 
            public Status call() throws Exception { return fakeTest(Status.ERROR("Cruel World")); }
        });

        assertEquals("APPLICATION_STATUS: ERROR Sub-components are broken. 1\n" +
                "Hello: OK Happy World\n" +
                "Hello2: ERROR Cruel World\n", 
                monitor.createMonitorReport());
    }

    @Test
    public void timeout() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor(1);
        long startTs = System.currentTimeMillis();    
        monitor.addCheck("Take", new Callable<Status>() { 
            public Status call() throws Exception { 
                Thread.sleep(2345);
                return Status.OK("Time out"); }
        });
        monitor.addCheck("Hello", new Callable<Status>() { 
            public Status call() throws Exception { 
                return Status.OK("Happy World"); }
        });
        String report = monitor.createMonitorReport();
        long elapsed = System.currentTimeMillis() - startTs;

        assertTrue("Elapsed should be approx 1 s, is " + elapsed, elapsed < 1300 && elapsed > 700);
        assertTrue(report, report.startsWith("APPLICATION_STATUS: ERROR Sub-components are broken. 1"));
        assertTrue(report, report.contains("Take: ERROR Timeout executing test after")); //ignore ms suffix
        assertTrue(report, report.contains("Hello: OK Happy World")); //make sure other tests still pass completed
    }


    @Test
    public void duplicateCheckKeysNotAllowed() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor();

        monitor.addCheck("Hello", new Callable<Status>() { 
            public Status call() throws Exception { return fakeTest(Status.OK("Happy World")); }
        });
        try {
            monitor.addCheck("Hello", new Callable<Status>() { 
                public Status call() throws Exception { return fakeTest(Status.ERROR("Cruel World")); }
            });
            fail("Duplicate key should not be allowed");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Implicit redefintion of exiting key"));
        }
    }
    
    protected Status fakeTest(Status statusToReturn) {
        return statusToReturn;
    }

    @Test
    public void noChecksNotOkStatus() throws IOException {
        ApplicationMonitor monitor = new ApplicationMonitor();
        assertEquals("APPLICATION_STATUS: ERROR No checks configured\n", monitor.createMonitorReport());
    }}
