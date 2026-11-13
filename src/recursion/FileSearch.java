package recursion;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Task 1: searches a directory and all its subdirectories for files by name.
 *
 * Usage: java recursion.FileSearch [-i] directory fileName [fileName ...]
 *   -i  search ignoring case (default is case-sensitive)
 */
public class FileSearch {

    /**
     * Searches dir and all of its subdirectories for files with the given names.
     *
     * @param dir an existing directory
     * @param fileNames names to look for, each non-empty, at least one
     * @param caseSensitive true to match names exactly, false to ignore case
     * @return a map with one entry for each name in fileNames (same order); the value is the
     *         list of full paths of matching files, empty if the file was not found
     * @throws IllegalArgumentException if dir does not exist or is not a directory,
     *         or fileNames is empty or contains an empty name
     */
    public static Map<String, List<String>> search(File dir, List<String> fileNames, boolean caseSensitive) {
        if (dir == null || !dir.exists()) {
            throw new IllegalArgumentException("directory does not exist: " + dir);
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        if (fileNames == null || fileNames.isEmpty()) {
            throw new IllegalArgumentException("no file name given");
        }
        // key = name we compare against, value = paths found so far
        Map<String, List<String>> found = new LinkedHashMap<>();
        for (String name : fileNames) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("file name is empty");
            }
            found.putIfAbsent(key(name, caseSensitive), new ArrayList<>());
        }
        searchDir(dir, found, caseSensitive); // one walk for all names, not one per name

        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String name : fileNames) {
            result.put(name, found.get(key(name, caseSensitive)));
        }
        return result;
    }

    /**
     * Counts how many times a file appears in dir and its subdirectories.
     *
     * @param dir an existing directory
     * @param fileName non-empty name to look for
     * @param caseSensitive true to match the name exactly, false to ignore case
     * @return number of files named fileName, 0 if none
     * @throws IllegalArgumentException same cases as search()
     */
    public static int count(File dir, String fileName, boolean caseSensitive) {
        return search(dir, Arrays.asList(fileName), caseSensitive).get(fileName).size();
    }

    // recursive helper
    // base case: dir has no entries (or cant be read) so nothing to do
    // recursive step: check the files here, then call searchDir on each sub folder
    private static void searchDir(File dir, Map<String, List<String>> found, boolean caseSensitive) {
        File[] entries = dir.listFiles();
        if (entries == null || entries.length == 0) {
            return;
        }
        Arrays.sort(entries); // listFiles order is not fixed, sort so output is same every run
        for (File entry : entries) {
            if (entry.isDirectory()) {
                if (!Files.isSymbolicLink(entry.toPath())) { // dont follow links, they can loop forever
                    searchDir(entry, found, caseSensitive);
                }
            } else {
                List<String> paths = found.get(key(entry.getName(), caseSensitive));
                if (paths != null) {
                    paths.add(entry.getAbsolutePath());
                }
            }
        }
    }

    private static String key(String name, boolean caseSensitive) {
        return caseSensitive ? name : name.toLowerCase(Locale.ROOT);
    }

    public static void main(String[] args) {
        List<String> rest = new ArrayList<>(Arrays.asList(args));
        boolean caseSensitive = !rest.remove("-i");
        if (rest.size() < 2) {
            System.out.println("Usage: java recursion.FileSearch [-i] <directory> <fileName> [fileName ...]");
            return;
        }
        File dir = new File(rest.get(0));
        List<String> names = rest.subList(1, rest.size());
        try {
            Map<String, List<String>> result = search(dir, names, caseSensitive);
            System.out.println("Searching " + dir.getAbsolutePath()
                    + (caseSensitive ? " (case-sensitive)" : " (case-insensitive)"));
            for (Map.Entry<String, List<String>> e : result.entrySet()) {
                List<String> paths = e.getValue();
                if (paths.isEmpty()) {
                    System.out.println(e.getKey() + ": not found");
                } else {
                    System.out.println(e.getKey() + ": found " + paths.size() + " time(s)");
                    for (String path : paths) {
                        System.out.println("    " + path);
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (SecurityException ex) {
            System.out.println("Error: no permission to read " + dir);
        }
    }
}
