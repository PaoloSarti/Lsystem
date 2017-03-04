package l_system.processing;

import java.util.List;

/**
 * Created by paolo on 04/03/2017.
 */
public interface LSystemCalc {
    String lsystem(String axiom, List<String> rules, int iterations);

    String lsystem(String axiom, List<String> rules, int iterations, double probabilityToMiss);

    void stop();
}
