package com.fast.fastrpc.remoting.netty;

import com.fast.fastrpc.Timeout;
import com.fast.fastrpc.common.PrefixThreadFactory;
import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * @author yiji
 * @version : Timer.java, v 0.1 2020-08-06
 */
public class Timer {

    private final static long defaultTickDuration = 10;

    private Timer() {
    }

    /**
     * Get a singleton instance of {@link io.netty.util.Timer}. <br>
     * The tick duration is {@link #defaultTickDuration}.
     *
     * @return Timer
     */
    public static io.netty.util.Timer getTimer() {
        return DefaultInstance.INSTANCE;
    }

    public static Timeout createTimeout(final TimeoutTask task, int timeout) {
        return new TimeoutImpl(getTimeout(task, timeout));
    }

    private static io.netty.util.Timeout getTimeout(final TimeoutTask task, int timeout) {
        return getTimer().newTimeout(new TimerTask() {
            @Override
            public void run(io.netty.util.Timeout timeout) throws Exception {
                task.execute(new TimeoutImpl(timeout));
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }

    private static class DefaultInstance {
        static final io.netty.util.Timer INSTANCE = new HashedWheelTimer(new PrefixThreadFactory(
                "DefaultTimer" + defaultTickDuration), defaultTickDuration, TimeUnit.MILLISECONDS);
    }

    private static class TimeoutImpl implements Timeout {

        private io.netty.util.Timeout timeout;

        public TimeoutImpl(io.netty.util.Timeout timeout) {
            this.timeout = timeout;
        }

        @Override
        public boolean isExpired() {
            return this.timeout.isExpired();
        }

        @Override
        public boolean isCancelled() {
            return this.timeout.isCancelled();
        }

        @Override
        public boolean cancel() {
            return this.timeout.cancel();
        }
    }

}
