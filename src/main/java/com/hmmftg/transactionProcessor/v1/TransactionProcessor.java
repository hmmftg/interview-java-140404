package com.hmmftg.transactionProcessor.v1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class TransactionProcessor {
    public static void main(String[] args) {
        String filePath = args.length > 0 ? args[0] : "sample.txt";
        long start = System.currentTimeMillis();
        processTransactions(filePath);
        long end = System.currentTimeMillis();
        System.out.println("Execution took " + (end - start) + " ms");
    }

    public static void processTransactions(String filePath) {
        try (FileReader fr = new FileReader(filePath)) {
            processTransactions(fr);
        } catch (IOException e) {
            System.out.println("Error processing transactions: " + e.getMessage());
        }
    }

    public static void processTransactions(Reader br) {
        try (BufferedReader reader = new BufferedReader(br)) {
            String line;
            reader.readLine(); // Skip header
            Map<String, Transaction> transactionMap = new HashMap<>();
            long sum = 0;
            long count = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length != 3) {
                    continue;
                }

                String sourceAccount = parts[0].trim();
                long amount;
                try {
                    amount = Long.parseLong(parts[1].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid transaction amount: " + line);
                    continue;
                }

                String ssn = parts[2].trim();
                String key = sourceAccount + ":" + amount;

                transactionMap.put(key,
                        transactionMap.getOrDefault(key, new Transaction(sourceAccount, amount, ssn)).incrementCount());

                sum += amount;
                count++;
            }

            System.out.println("Count: " + count);
            System.out.println("Sum: " + sum);

            System.out.println("Duplicate Transactions:");
            for (Map.Entry<String, Transaction> entry : transactionMap.entrySet()) {
                if (entry.getValue().getCount() > 1) {
                    System.out.println(entry.getValue());
                }
            }
        } catch (IOException e) {
            System.out.println("Error processing transactions: " + e.getMessage());
        }
    }

    private static class Transaction {
        private final String sourceAccount;
        private final long amount;
        // private final String ssn;
        private int count;

        public Transaction(String sourceAccount, long amount, String ssn) {
            this.sourceAccount = sourceAccount;
            this.amount = amount;
            // this.ssn = ssn;
        }

        public String toString() {
            return "Source Account: " + sourceAccount + ", Amount: " + amount + ", Count: " + count;
        }

        public int getCount() {
            return count;
        }

        public Transaction incrementCount() {
            count++;
            return this;
        }
    }
}