package recursion;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class PermutationsTest {

    /*
     * Testing strategy (same for recursive and iterative version)
     *
     * length of s: 0 (invalid), 1 (base case), > 1 (recursive step)
     * characters: all different, some repeated, all the same
     * includeDuplicates: true, false
     * s is null (invalid)
     */

    @Test
    public void testSingleChar() {
        assertEquals(Arrays.asList("a"), Permutations.generatePermutations("a", true));
        assertEquals(Arrays.asList("a"), Permutations.generatePermutationsIterative("a", true));
    }

    @Test
    public void testDistinctChars() {
        Set<String> expected = new HashSet<>(Arrays.asList("abc", "acb", "bac", "bca", "cab", "cba"));
        List<String> perms = Permutations.generatePermutations("abc", true);
        assertEquals(6, perms.size());
        assertEquals(expected, new HashSet<>(perms));
    }

    @Test
    public void testDuplicatesIncluded() {
        List<String> perms = Permutations.generatePermutations("aab", true);
        assertEquals(6, perms.size()); // 3! even with the repeated a
        assertEquals(new HashSet<>(Arrays.asList("aab", "aba", "baa")), new HashSet<>(perms));
    }

    @Test
    public void testDuplicatesRemoved() {
        List<String> perms = Permutations.generatePermutations("aab", false);
        assertEquals(3, perms.size());
        assertEquals(new HashSet<>(Arrays.asList("aab", "aba", "baa")), new HashSet<>(perms));
    }

    @Test
    public void testAllSameChars() {
        assertEquals(Arrays.asList("aaa"), Permutations.generatePermutations("aaa", false));
        assertEquals(6, Permutations.generatePermutations("aaa", true).size());
    }

    @Test
    public void testCountIsFactorial() {
        assertEquals(720, Permutations.generatePermutations("abcdef", true).size());
        assertEquals(720, Permutations.generatePermutationsIterative("abcdef", true).size());
    }

    @Test
    public void testIterativeMatchesRecursive() {
        for (String s : Arrays.asList("ab", "abcd", "aabb", "abca")) {
            for (boolean dup : new boolean[] {true, false}) {
                List<String> rec = Permutations.generatePermutations(s, dup);
                List<String> it = Permutations.generatePermutationsIterative(s, dup);
                assertEquals(s, rec.size(), it.size());
                assertEquals(s, new HashSet<>(rec), new HashSet<>(it));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyString() {
        Permutations.generatePermutations("", true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullString() {
        Permutations.generatePermutations(null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyStringIterative() {
        Permutations.generatePermutationsIterative("", false);
    }
}
