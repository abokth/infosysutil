package se.kth.sys.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tool to generate a monitor response for diagnostics.
 *
 * This is intended to be used by multiple applications following
 * the convention used by monitoring tools at KTH. The end result
 * is a report like
 * <pre>
 * APPLICATION_STATUS: ERROR Sub-components are broken. 1
 * SCHEMA_BACKEND: ERROR Timeout executing test java.util.concurrent.TimeoutException
 * LOCAL_DB_CONNECTION: OK The sysdate is 2010-09-10.
 * </pre>
 * This is created by adding a number of checks that return a
 * <code>Status</code>. In the above example there are three checks provided,
 * this is then summarized in to the total
 * application status. Each check is run independently and with a timeout.
 *
 * @author peter.lundberg
 */
public class ApplicationMonitor {

    private static final long  MS_PER_SEC = 1000L;

    /**
     * Represents the result of a test, an immutable tuple of boolean & string.
     */
    public static final class Status {
        private final boolean isOk;
        private final String message;

        public static Status OK(String message) {
            return new Status(true, message);
        }

        public static Status ERROR(String message) {
            return new Status(false, message);
        }

        private Status(boolean isOk, String message) {
            this.isOk = isOk;
            this.message = message;
        }

        public boolean isOk() {
            return isOk;
        }

        public String toString() {
            if (isOk) {
                return "OK " + message;
            } else {
                return "ERROR " + message;
            }
        }
    }

    private ExecutorService executorService =
                    Executors.newCachedThreadPool(); //unbounded
    private Map<String, Future<Status>> testFutures =
                    new HashMap<String, Future<Status>>();
    private List<String> checkOrder =
                    new ArrayList<String>();
    private int maxReportTimeSecs;

    /**
     * Create an ApplicationMonitor with a default
     * <code>maxReportTimeSecs</code> of 15 seconds.
     */
    public ApplicationMonitor() {
        this(15);
    }

    public ApplicationMonitor(int maxReportTimeSecs) {
        this.maxReportTimeSecs = maxReportTimeSecs;
    }

    /**
     * Add check to be part of the total application status.
     * @param statusName unique identifier of the check/subcomponent
     * @param callable the actual check that will get called in another thread
     */
    public final void addCheck(final String statusName,
                        final Callable<Status> callable) {
        if (testFutures.containsKey(statusName)) {
            // this will catch simple errors
            throw new IllegalArgumentException("Implicit redefintion of "
                    + "existing key '" + statusName + "' not allowed.");
        }
        checkOrder.add(statusName);
        testFutures.put(statusName, executorService.submit(callable));
    }

    /**
     * Generate the standard applicaiton status report based on added checks.
     * Will return within <code>maxReportTimeSecs</code>
     * @return the monitor report
     * @throws IOException in unlikely situation it cannot build the string
     */
    public final String createMonitorReport() throws IOException {
        StringWriter totalResult = new StringWriter();
        createMonitorReport(totalResult);
        return totalResult.toString();
    }

    /**
     * Generate the standard applicaiton status report based on added checks.
     * Will return within <code>maxReportTimeSecs</code>
     * @param output a writer to print the report to.
     * @return true if the total status is OK
     * @throws IOException in unlikely situation it cannot build the string
     */
    private Status createMonitorReport(Writer output) throws IOException {
        // get the results from all tests (with timeouts)
        StringWriter detailedResults = new StringWriter();
        int errors = checkAllFutures(detailedResults);

        // Output
        Status result;
        if (errors > 0) {
            result = Status.ERROR("Sub-components are broken. " + errors);
        } else if (testFutures.size() > 0) {
            result = Status.OK("Every component is working");
        } else {
            result = Status.ERROR("No checks configured");
        }
        output.append("APPLICATION_STATUS: ");
        output.append(result.toString());
        output.append("\n");
        output.append(detailedResults.getBuffer());

        //Cleanup
        executorService.shutdown();

        return result;
    }


    private int checkAllFutures(Writer sw) throws IOException {
        final long startTS = System.currentTimeMillis();
        int errors = 0;
        // For each entered check
        for (String checkName : checkOrder) {
            // Get the result, with a time out
            Status result;
            try {
                Future<Status> future = this.testFutures.get(checkName);
                long timeout = startTS
                        + (maxReportTimeSecs * MS_PER_SEC)
                        - System.currentTimeMillis();
                if (timeout < 0) {
                    timeout = 0;
                }
                result = future.get(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                result = Status.ERROR("Interupted executing test " + e);
            } catch (ExecutionException e) {
                result = Status.ERROR("Exception executing test " + e.getCause());
            } catch (TimeoutException e) {
                long elapsed = System.currentTimeMillis() - startTS;
                result = Status.ERROR("Timeout executing test "
                        + "after " + elapsed + "ms");
            }

            // remember total nr errors
            if (!result.isOk) {
                errors++;
            }

            // Format human and script readable output message
            sw.append(checkName);
            sw.append(": ");
            sw.append(result.toString().replace("\n", "\\n"));
            sw.append("\n");
        }
        return errors;
    }


}
