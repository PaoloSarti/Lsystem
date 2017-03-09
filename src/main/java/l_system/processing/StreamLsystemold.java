package l_system.processing;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamLsystemold implements LSystemCalc{
    private boolean stopped = false;

    class CharReplace{
        char c;
        boolean replaced;
        CharReplace(char c, boolean replaced) {
            this.c = c;
            this.replaced = replaced;
        }
    }

    public String lsystem(String axiom, List<String> rules, int iterations){
        return lsystem(axiom, rules, iterations, 0, 0L);
    }

    public String lsystem(String axiom, List<String> rules, int iterations, double probabilityToMiss, long seed) {
        stopped=false;
        Random random = new Random(seed);
        Map<Character,String> charStrings = charStringMap(axiom, rules);
        Stream<CharReplace> lsys = lsList(axiom, false).parallelStream();
        Map<Character, String> splittedRules = splitRules(rules);
        for(int i=0; i<iterations && !stopped; i++){
            for(Character c : splittedRules.keySet()){
                if(!stopped)
                    lsys = applyRule(lsys, c, splittedRules.get(c), probabilityToMiss, random);
            }
            lsys = lsys.map(cr->{
                cr.replaced=false;
                return cr;
            });
        }
        return lsys.map(cr->charStrings.get(cr.c)).collect(Collectors.joining());//joinChars(lsys.collect(Collectors.toList()));
    }

    private Stream<CharReplace> applyRule(Stream<CharReplace> lsys, Character ch, String s, double probabilityToMiss, Random random) {
        return lsys.parallel().flatMap(c->{
            if(stopped){
                throw new RuntimeException();
            }
            if(!c.replaced&&c.c == ch && random.nextDouble()>probabilityToMiss){
                return lsList(s, true).stream();
            }
            else {
                return Stream.of(c);
            }
        });
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

    private List<CharReplace> lsList(String s, boolean b) {
        List<CharReplace> chars = new ArrayList<>();
        for(char c : s.toCharArray()){
            chars.add(new CharReplace(c, b));
        }
        return chars;
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