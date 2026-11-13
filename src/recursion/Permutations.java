package recursion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Task 2: all permutations of a string, recursive and non-recursive.
 *
 * Usage: java recursion.Permutations string [-nodup]
 *        java recursion.Permutations -compare
 */
public class Permutations {

    /**
     * Generates all permutations of s using recursion.
     *
     * @param s a non-empty string
     * @param includeDuplicates true to return all n! arrangements (a repeated character gives
     *        repeated permutations), false to return each different permutation only once
     * @return list of the permutations, in no particular order
     * @throws IllegalArgumentException if s is null or empty
     */
    public static List<String> generatePermutations(String s, boolean includeDuplicates) {
        check(s);
        List<String> result = new ArrayList<>();
        permute(s.toCharArray(), 0, includeDuplicates, result);
        return result;
    }

    // base case: start is at the last char, only one way to arrange it so save the string
    // recursive step: put every char (one by one) at position start by swaping,
    // then permute the rest starting from start+1, then swap back
    private static void permute(char[] chars, int start, boolean includeDuplicates, List<String> result) {
        if (start == chars.length - 1) {
            result.add(new String(chars));
            return;
        }
        Set<Character> used = new HashSet<>(); // chars already tried at this position
        for (int i = start; i < chars.length; i++) {
            if (!includeDuplicates && !used.add(chars[i])) {
                continue; // same char again here would give the same permutations twice
            }
            swap(chars, start, i);
            permute(chars, start + 1, includeDuplicates, result);
            swap(chars, start, i);
        }
    }

    /**
     * Generates all permutations of s without recursion (Heap's algorithm).
     * Same spec as generatePermutations().
     *
     * @param s a non-empty string
     * @param includeDuplicates true for all n! arrangements, false for each different one once
     * @return list of the permutations, in no particular order
     * @throws IllegalArgumentException if s is null or empty
     */
    public static List<String> generatePermutationsIterative(String s, boolean includeDuplicates) {
        check(s);
        char[] chars = s.toCharArray();
        int n = chars.length;
        int[] c = new int[n]; // c[i] is the loop counter that recursion would keep on the stack
        List<String> result = new ArrayList<>();
        result.add(new String(chars));
        int i = 1;
        while (i < n) {
            if (c[i] < i) {
                swap(chars, i % 2 == 0 ? 0 : c[i], i);
                result.add(new String(chars));
                c[i]++;
                i = 1;
            } else {
                c[i] = 0;
                i++;
            }
        }
        if (!includeDuplicates) {
            return new ArrayList<>(new LinkedHashSet<>(result));
        }
        return result;
    }

    private static void check(String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("input string is empty");
        }
    }

    private static void swap(char[] chars, int a, int b) {
        char t = chars[a];
        chars[a] = chars[b];
        chars[b] = t;
    }

    // times both versions for strings of length 6 to 10 (11 needs too much memory)
    private static void compare() {
        String letters = "abcdefghij";
        generatePermutations("abcdefg", true); // warm up the JIT first so times are fair
        generatePermutationsIterative("abcdefg", true);
        System.out.printf("%-4s %-10s %-15s %-15s%n", "n", "n!", "recursive(ms)", "iterative(ms)");
        for (int n = 6; n <= 10; n++) {
            String s = letters.substring(0, n);
            long t0 = System.nanoTime();
            int count = generatePermutations(s, true).size();
            long t1 = System.nanoTime();
            generatePermutationsIterative(s, true);
            long t2 = System.nanoTime();
            System.out.printf("%-4d %-10d %-15d %-15d%n", n, count, (t1 - t0) / 1000000, (t2 - t1) / 1000000);
        }
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-compare")) {
            compare();
            return;
        }
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java recursion.Permutations <string> [-nodup] | -compare");
            return;
        }
        boolean includeDuplicates = !(args.length == 2 && args[1].equals("-nodup"));
        try {
            List<String> perms = generatePermutations(args[0], includeDuplicates);
            System.out.println(perms.size() + " permutations of \"" + args[0] + "\""
                    + (includeDuplicates ? " (duplicates included)" : " (duplicates removed)"));
            System.out.println(perms);
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
