package com.hmmftg;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PerformanceTest {
    // expected output constants
    private static final long EXPECTED_COUNT = 516L;
    private static final long EXPECTED_SUM   = 71199785618937L;
    private static final List<String> EXPECTED_DUPLICATES = Arrays.asList(
        "Source Account: 12345467890123, Amount: 1000010000000, Count: 28",
        "Source Account: 12345467890123, Amount: 313926263, Count: 146",
        "Source Account: 12345467890123, Amount: 1000000000000, Count: 43"
    );

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of iterations per version: ");
        int iterations = scanner.nextInt();
        scanner.close();

        String filePath = "sample.txt"; 

        List<String> versions = Arrays.asList(
            "v1.TransactionProcessor",  // Version 1
            "v2.TransactionProcessor",   // Version 2
            "v3.TransactionProcessor"      // Version 3
        );

        for (String version : versions) {
            System.out.println("=== Testing " + version + " ===");

            boolean allCorrect = true;
            String failureReason = "";

            // warm-up (optional, can help JIT)
            for (int i = 0; i < 2; i++) {
                captureOutput(() -> runVersion(version, filePath));
            }

            // now measure N iterations in one go
            long startTime = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                String output = captureOutput(() -> runVersion(version, filePath));
                if (allCorrect) {
                    Report r = checkOutput(output);
                    if (!r.isCorrect) {
                        allCorrect = false;
                        failureReason = r.reason + " on iteration " + (i+1);
                    }
                }
            }
            long totalNanos = System.nanoTime() - startTime;
            double totalMs = totalNanos / 1_000_000.0;
            double avgMs   = totalMs / iterations;

            // print result
            System.out.printf("Result: %s\n", allCorrect ? "OK" : "NOK (" + failureReason + ")");
            System.out.printf("Total time: %.3f ms  |  Avg time: %.3f ms\n\n", totalMs, avgMs);
        }
    }

    /** Captures System.out during the task and returns it as a String. */
    public static String captureOutput(Runnable task) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos)) {
            System.setOut(ps);
            task.run();
        } finally {
            System.setOut(originalOut);
        }
        return baos.toString().replaceAll("\r", "");
    }

    /** Invokes the main(String[]) of each version via reflection. */
    public static void runVersion(String className, String filePath) {
        try {
            Class<?> cls = Class.forName(className);
            cls.getMethod("main", String[].class)
               .invoke(null, (Object) new String[]{ filePath });
        } catch (Exception e) {
            // we'll capture this in the output check
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /** Simple pass/fail report for correctness. */
    private static class Report {
        final boolean isCorrect;
        final String reason;
        Report(boolean ok, String why) { isCorrect = ok; reason = why; }
    }

    /** Parses the output string and verifies it matches expected. */
    public static Report checkOutput(String out) {
        String[] lines = out.split("\n");
        if (lines.length < 4) {
            return new Report(false, "too few lines (" + lines.length + ")");
        }

        // check count line
        if (!lines[0].equals("Count: " + EXPECTED_COUNT)) {
            return new Report(false, "bad count: " + lines[0]);
        }
        // check sum line
        if (!lines[1].equals("Sum: " + EXPECTED_SUM)) {
            return new Report(false, "bad sum: " + lines[1]);
        }
        // check header
        if (!lines[2].equals("Duplicate Transactions:")) {
            return new Report(false, "bad dup header: " + lines[2]);
        }

        // gather duplicates
        List<String> found = Arrays.asList(Arrays.copyOfRange(lines, 3, lines.length));
        if (found.size() != EXPECTED_DUPLICATES.size()) {
            return new Report(false,
                "dup count mismatch: expected " 
                + EXPECTED_DUPLICATES.size() + ", got " + found.size());
        }

        for (String exp : EXPECTED_DUPLICATES) {
            if (!found.contains(exp)) {
                return new Report(false, "missing dup: " + exp);
            }
        }

        return new Report(true, "");
    }
}
