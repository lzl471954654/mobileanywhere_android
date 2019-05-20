package io.liuzhilin.mobileanywhere.manager;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadManager {

    public static ThreadManager threadManager = new ThreadManager();

    private Thread netWoker;

    private ThreadManager(){
        networkTask = new LinkedBlockingQueue<>();
        netWoker = new Thread(networkRun,"NetWork-Thread");
        netWoker.start();
    }

    private LinkedBlockingQueue<Runnable> networkTask;

    private Runnable networkRun  = ()->{
        while (true){
            try{
                Runnable runnable = networkTask.take();
                runnable.run();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public static ThreadManager getThreadManager() {
        return threadManager;
    }

    public void addNetworkTask(Runnable runnable){
        try {
            networkTask.put(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
