package l_system.processing;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LSystemProcessing {
    LSystemCalc ls;

    public LSystemProcessing(LSystemCalc ls){
        this.ls=ls;
    }

    public void startProcessing(String axiom, List<String> rules, int n, double probabilityToMiss, long seed, StringProcessingListener controller){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(()->{
            try {
                String command = ls.lsystem(axiom, rules, n, probabilityToMiss, seed);
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
