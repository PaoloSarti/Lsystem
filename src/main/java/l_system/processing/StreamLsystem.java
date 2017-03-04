package l_system.processing;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamLsystem implements LSystemCalc{
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
        return lsystem(axiom, rules, iterations, 0);
    }

    public String lsystem(String axiom, List<String> rules, int iterations, double probabilityToMiss) {
        stopped=false;
        Stream<CharReplace> lsys = lsList(axiom, false).parallelStream();
        Map<Character, String> splittedRules = splitRules(rules);
        for(int i=0; i<iterations && !stopped; i++){
            for(Character c : splittedRules.keySet()){
                if(!stopped)
                    lsys = applyRule(lsys, c, splittedRules.get(c), probabilityToMiss);
            }
            lsys = lsys.map(cr->{
                cr.replaced=false;
                return cr;
            });
        }
        return joinChars(lsys.collect(Collectors.toList()));
    }

    private Stream<CharReplace> applyRule(Stream<CharReplace> lsys, Character ch, String s, double probabilityToMiss) {
        return lsys.parallel().flatMap(c->{
            if(stopped){
                throw new RuntimeException();
            }
            if(!c.replaced&&c.c == ch && Math.random()>probabilityToMiss){
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

    private String joinChars(List<CharReplace> lsys) {
        StringBuilder s = new StringBuilder();
        for(CharReplace cr : lsys){
            s.append(cr.c);
        }
        return s.toString();
    }

    /*
    private String joinChars(Stream<CharReplace> chars){
        chars.map(cr->cr.c).reduce("",(a,b)->a.toString()+b.toString());
    }*/

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
}