package pl.setblack.wth.forkjoin;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

class SpyExecutor extends ForkJoinPool {


    private final Runnable action;

    //copy from ForkJoinPool
    static final int parallelism = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    private final Executor delegate = Executors.newFixedThreadPool(parallelism);

    public SpyExecutor(Runnable action) {
        this.action = action;
    }

    @Override
    public void execute(Runnable task) {
        action.run();
        delegate.execute(() -> {
            System.out.println("I'm executing a task in my pool");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            super.execute(task);
        });

    }
}
