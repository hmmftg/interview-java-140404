package com.hmmftg.transactionProcessor.v2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class TransactionProcessor {
    private static class TransactionKey {
        private final String sourceAccount;
        private final long amount;

        public TransactionKey(String sourceAccount, long amount) {
            this.sourceAccount = sourceAccount.intern(); // Intern the string
            this.amount = amount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof TransactionKey))
                return false;
            TransactionKey that = (TransactionKey) o;
            return amount == that.amount && sourceAccount.equals(that.sourceAccount);
        }

        @Override
        public int hashCode() {
            return 31 * sourceAccount.hashCode() + Long.hashCode(amount);
        }
    }

    private static class Transaction {
        private final String sourceAccount;
        private final long amount;
        private int count;

        public Transaction(String sourceAccount, long amount) {
            this.sourceAccount = sourceAccount;
            this.amount = amount;
        }

        public void incrementCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return String.format("Source Account: %s, Amount: %d, Count: %d", sourceAccount, amount, count);
        }
    }

    public static void main(String[] args) {
        String filePath = "sample.txt"; // Update with your file path
        long startTime = System.currentTimeMillis();
        String result = processTransactions(filePath);
        long endTime = System.currentTimeMillis();
        System.out.println("Execution Time: " + (endTime - startTime) + " milliseconds");
        System.out.println(result);
    }

    public static String processTransactions(String filePath) {
        try (FileReader fr = new FileReader(filePath)) {
            return processTransactions(fr);
        } catch (IOException e) {
            return "Error processing transactions: " + e.getMessage();
        }
    }

    public static String processTransactions(Reader br) {
        Map<TransactionKey, Transaction> transactionMap = new HashMap<>();
        long totalAmount = 0;
        long totalRows = 0;

        try (BufferedReader reader = new BufferedReader(br)) {
            reader.readLine(); // Skip header

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue; // Skip empty lines

                // Manual parsing instead of split
                int tabIndex1 = line.indexOf('\t');
                int tabIndex2 = line.indexOf('\t', tabIndex1 + 1);
                if (tabIndex1 == -1 || tabIndex2 == -1)
                    continue; // Ensure we have the correct number of parts

                String sourceAccount = line.substring(0, tabIndex1).trim().intern(); // Intern the string
                long amount;
                // try {
                amount = Long.parseLong(line.substring(tabIndex1 + 1, tabIndex2).trim());
                // } catch (NumberFormatException e) {
                // System.err.println("Invalid transaction amount: " + line);
                // continue;
                // }

                TransactionKey key = new TransactionKey(sourceAccount, amount);
                transactionMap.computeIfAbsent(key, k -> new Transaction(sourceAccount, amount)).incrementCount();

                totalAmount += amount;
                totalRows++;
            }

            // Output results
            StringBuilder output = new StringBuilder();
            output.append("Count: ").append(totalRows).append("\n");
            output.append("Sum: ").append(totalAmount).append("\n");
            output.append("Duplicate Transactions:\n");
            for (Transaction transaction : transactionMap.values()) {
                if (transaction.getCount() > 1) {
                    output.append(transaction).append("\n");
                }
            }
            return output.toString();
        } catch (IOException e) {
            return "Error processing transactions: " + e.getMessage();
        }
    }
}
