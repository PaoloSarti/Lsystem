package l_system.processing;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamLsystem implements LSystemCalc{
    private boolean stopped = false;

    public String lsystem(String axiom, List<String> rules, int iterations){
        return lsystem(axiom, rules, iterations, 0, 0L);
    }

    public String lsystem(String axiom, List<String> rules, int iterations, double probabilityToMiss, long seed) {
        stopped=false;
        Random random = new Random(seed);
        Map<Character,String> charStrings = charStringMap(axiom, rules);
        Stream<Character> lsys = axiom.chars().mapToObj(i->(char) i);
        Map<Character, String> splittedRules = splitRules(rules);
        for(int i=0; i<iterations && !stopped; i++){
            lsys = lsys.parallel().flatMap(c->{
                if(stopped){
                    throw new RuntimeException();
                }
                if(splittedRules.containsKey(c)&&random.nextDouble()>probabilityToMiss){
                    return splittedRules.get(c).chars().mapToObj(ch->(char) ch).parallel();
                }
                else{
                    return Stream.of(c);
                }
            });
        }
        return lsys.map(charStrings::get).collect(Collectors.joining());
    }

    public void stop(){
        this.stopped=true;
    }

    private Map<Character,String> splitRules(List<String> rules) {
        Map<Character, String> m = new HashMap<>();
        for(String rule : rules){
            String[] splitted = rule.split("=");
            m.put(splitted[0].charAt(0), splitted[1]);
        }
        return m;
    }

    private Map<Character,String> charStringMap(String axiom, List<String> rules){
        Map<Character, String> map = new HashMap<>();
        putCharsInMap(axiom, map);
        for(String rule : rules){
            putCharsInMap(rule, map);
        }
        return map;
    }

    private void putCharsInMap(String chars, Map<Character,String> map){
        for(char c : chars.toCharArray()){
            map.put(c,String.valueOf(c));
        }
    }
}