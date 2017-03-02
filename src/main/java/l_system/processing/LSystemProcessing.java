package l_system.processing;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LSystemProcessing {
    StreamLsystem ls = new StreamLsystem();

    public void startProcessing(String axiom, List<String> rules, int n, double probabilityToMiss, StringProcessingListener controller){
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executor.execute(()->{
            try {
                String command = ls.lsystem(axiom, rules, n, probabilityToMiss);
                controller.finishedStringProcessing(command);
            } catch (RuntimeException re){
                System.gc();
            }
        });
    }

    public void stop(){
        ls.stop();
    }
}
