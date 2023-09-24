package party.zonarius.advent2022;

import party.zonarius.advent2022.common.AdventInput;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        String input = AdventInput.input().findFirst().orElseThrow();

        int len = 14;
        Counter<Character> counter = new Counter<>();
        int i = 0;
        char[] inputC = input.toCharArray();
        for (char c : inputC) {
            if (i - len >= 0) {
                counter.dec(inputC[i - len]);
            }
            counter.inc(c);
            if (counter.size() == len) {
                System.out.println(i + 1);
                break;
            }
            i++;
        }
    }

    static private class Counter<T> {
        private final HashMap<T, Integer> map;

        public Counter() {
            map = new HashMap<>();
        }

        public void inc(T elem) {
            map.put(elem, map.getOrDefault(elem, 0) + 1);
        }

        public void dec(T elem) {
            map.put(elem, map.getOrDefault(elem, 0) - 1);
            if (map.get(elem) == 0) {
                map.remove(elem);
            }
        }

        public int size() {
            return map.size();
        }
    }
}