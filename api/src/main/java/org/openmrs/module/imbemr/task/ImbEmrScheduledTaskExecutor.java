package org.openmrs.module.imbemr.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;
import org.springframework.stereotype.Component;

/**
 * Executor that is responsible for scheduling and running the pihcore scheduled tasks
 */
@Component
public class ImbEmrScheduledTaskExecutor extends ScheduledExecutorFactoryBean {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final long oneSecond = 1000;
    private static final long oneMinute = oneSecond * 60;
    private static final long fiveMinutes = oneMinute * 5;
    private static final long oneHour = oneMinute * 60;

    public ImbEmrScheduledTaskExecutor() {
        setScheduledExecutorTasks(
                task(fiveMinutes, oneHour, ImbEmrCloseVisitsTask.class)
        );
    }

    private ScheduledExecutorTask task(long delay, long period, Class<? extends Runnable> runnable) {
        log.info("Scheduling task " + runnable.getSimpleName() + " with delay " + delay + " and period " + period);
        ScheduledExecutorTask task = new ScheduledExecutorTask();
        task.setDelay(delay);
        task.setPeriod(period);
        task.setRunnable(new ImbEmrTimerTask(runnable));
        return task;
    }
}
