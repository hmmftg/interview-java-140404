package com.hmmftg.transactionProcessor.v3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class TransactionProcessor {
    public static void main(String[] args) throws IOException {
        String filePath = "sample.txt";
        long start = System.nanoTime();
        String report;
        try (FileReader fr = new FileReader(filePath)) {
            report = processTransactions(fr);
        } catch (IOException e) {
            System.out.println("Error processing transactions: " + e.getMessage());
            return;
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        System.out.println(report);
        System.out.println("Elapsed: " + elapsedMs + " ms");
    }

    static class Report {
        long totalRows;
        long totalSum;
        String sourceAccount;
        Map<Long, int[]> duplicates; // amount -> [count]

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Count: ").append(totalRows).append("\n")
                    .append("Sum: ").append(totalSum).append("\n")
                    .append("Duplicate Transactions:\n");
            for (Map.Entry<Long, int[]> e : duplicates.entrySet()) {
                int c = e.getValue()[0];
                if (c > 1) {
                    sb.append("Source Account: ")
                            .append(sourceAccount)
                            .append(", Amount: ")
                            .append(e.getKey())
                            .append(", Count: ")
                            .append(c)
                            .append("\n");
                }
            }
            return sb.toString();
        }
    }

    public static String processTransactions(String filePath) {
        try (FileReader fr = new FileReader(filePath)) {
            String reportData=processTransactions(fr);
            return reportData;
        } catch (IOException e) {
            return "Error processing transactions: " + e.getMessage();
        }
    }

    public static String processTransactions(Reader br) {
        try {
            Report rep=process(br);
            return rep.toString();
        } catch (IOException e) {
            return "Error processing transactions: " + e.getMessage();
        }
    }

    public static Report process(Reader br) throws IOException {
        // We expect at most ~200 distinct amounts here; tweak if you know it better
        Map<Long, int[]> countMap = new HashMap<>(256);

        Report rep = new Report();
        rep.totalRows = 0;
        rep.totalSum = 0;

        try (BufferedReader r = new BufferedReader(br, 32_768)) {
            String line = r.readLine(); // skip header
            if (line == null)
                return rep;

            // read first data line to capture sourceAccount
            line = r.readLine();
            if (line == null)
                return rep;

            String[] parts = line.split("\t", 3);
            String srcAcct = parts[0];
            rep.sourceAccount = srcAcct;
            long amt = Long.parseLong(parts[1]);
            rep.totalRows = 1;
            rep.totalSum = amt;
            countMap.put(amt, new int[] { 1 });

            // process remaining lines
            while ((line = r.readLine()) != null) {
                // if (line.isEmpty()) continue;
                parts = line.split("\t", 3);
                // no need to check parts[0]—it’s always the same account
                amt = Long.parseLong(parts[1]);
                rep.totalRows++;
                rep.totalSum += amt;

                int[] slot = countMap.get(amt);
                if (slot == null) {
                    countMap.put(amt, new int[] { 1 });
                } else {
                    slot[0]++;
                }
            }
        }

        rep.duplicates = countMap;
        return rep;
    }
}
