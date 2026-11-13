package recursion;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileSearchTest {

    /*
     * Testing strategy
     *
     * directory: empty (base case), files only, nested sub folders (recursive step),
     *            does not exist, is a file
     * matches: 0, 1, > 1 (count)
     * case: sensitive, insensitive
     * number of names: 1, > 1
     */

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File root;

    // root/a.txt, root/sub/a.txt, root/sub/deep/A.TXT, root/sub/b.txt
    @Before
    public void makeTree() throws IOException {
        root = tmp.newFolder("root");
        new File(root, "sub/deep").mkdirs();
        new File(root, "a.txt").createNewFile();
        new File(root, "sub/a.txt").createNewFile();
        new File(root, "sub/deep/A.TXT").createNewFile();
        new File(root, "sub/b.txt").createNewFile();
    }

    private List<String> find(String name, boolean caseSensitive) {
        return FileSearch.search(root, Arrays.asList(name), caseSensitive).get(name);
    }

    @Test
    public void testEmptyDirectory() throws IOException {
        File empty = tmp.newFolder("empty");
        assertTrue(FileSearch.search(empty, Arrays.asList("a.txt"), true).get("a.txt").isEmpty());
    }

    @Test
    public void testFoundInSubfolder() {
        List<String> paths = find("b.txt", true);
        assertEquals(1, paths.size());
        assertEquals(new File(root, "sub/b.txt").getAbsolutePath(), paths.get(0));
    }

    @Test
    public void testNotFound() {
        assertTrue(find("missing.txt", true).isEmpty());
    }

    @Test
    public void testCountCaseSensitive() {
        assertEquals(2, FileSearch.count(root, "a.txt", true));
    }

    @Test
    public void testCountCaseInsensitive() {
        assertEquals(3, FileSearch.count(root, "a.txt", false));
        assertEquals(3, FileSearch.count(root, "A.Txt", false));
    }

    @Test
    public void testMultipleFiles() {
        Map<String, List<String>> result = FileSearch.search(root,
                Arrays.asList("a.txt", "b.txt", "c.txt"), true);
        assertEquals(Arrays.asList("a.txt", "b.txt", "c.txt"), Arrays.asList(result.keySet().toArray()));
        assertEquals(2, result.get("a.txt").size());
        assertEquals(1, result.get("b.txt").size());
        assertEquals(0, result.get("c.txt").size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectoryDoesNotExist() {
        FileSearch.search(new File(root, "nope"), Arrays.asList("a.txt"), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDirectoryIsAFile() {
        FileSearch.search(new File(root, "a.txt"), Arrays.asList("a.txt"), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyFileName() {
        FileSearch.search(root, Arrays.asList(""), true);
    }
}
