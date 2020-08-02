package blockchain;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BlockChain {
    private final int MIN_TIME = 10;
    private final int MAX_TIME = 60;

    private final Map<Integer, Block> blockMap;

    private int lastId;
    private int lastTransactionId;
    private String lastHash;
    private int zeros;
    private volatile int blocksToMake;
    private List<Transaction> newTransactions = new ArrayList<>();
    private List<Transaction> transactionsToBlock = new ArrayList<>();

    public BlockChain(Map<Integer, Block> blockMap) {
        this.blockMap = blockMap;
        lastId = blockMap.size();
        lastHash = blockMap.get(lastId).getHash();
        zeros = blockMap.get(lastId).getNextN();
        List<Transaction> allTransactions = blockMap.values().stream()
                .flatMap(b -> b.getBlockData().stream()).collect(Collectors.toList());
        if (allTransactions.size() == 0) {
            lastTransactionId = 0;
        } else {
            lastTransactionId = allTransactions.get(allTransactions.size() - 1).getId();
        }
    }

    public BlockChain() {
        blockMap = new TreeMap<>();
        lastHash = "0";
        lastId = 0;
        lastTransactionId = 0;
        zeros = 0;
    }

    public synchronized void addBlockToChain (Block block) {
        if (lastHash.equals(block.getPreviousHash()) && lastId + 1 == block.getId()) {
            lastId++;
            lastHash = block.getHash();
            int zeroChange = block.getCreationTimeSeconds() > MAX_TIME ? -1
                    : block.getCreationTimeSeconds() < MIN_TIME ? 1 : 0;
            zeros += zeroChange;
            block.setNextN(zeros);
            blocksToMake--;
            blockMap.put(lastId, block);
            transactionsToBlock = new ArrayList<>(newTransactions);
            newTransactions = new ArrayList<>();
            if (!validateBlockChain()) {
                System.out.println("chain invalid");
                throw new IllegalStateException("Chain is not valid, but it should :(");
            }
            try {
                SerializationUtils.serialize(blockMap, "chain.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("\n" + block);
        }
    }

    public synchronized void addTransaction(Transaction transaction) {
        if (!validateTransactions(transaction)) {
            return;
        }
        lastTransactionId++;
        transaction.setId(lastTransactionId);
        newTransactions.add(transaction);
    }

    public boolean validateBlockChain() {
        for (Block block : blockMap.values()) {
            if (block.getId() == 1) {
                continue;
            }
            Block previousBlock = blockMap.get(block.getId() - 1);
            if (!block.getPreviousHash().equals(previousBlock.getHash()))
                return false;
        }
        return true;
    }

    private boolean validateTransactions(Transaction newTransaction) {
        List<Transaction> allTransactions = blockMap.values().stream()
                .flatMap(b -> b.getBlockData().stream()).collect(Collectors.toList());
        allTransactions.addAll(transactionsToBlock);
        allTransactions.addAll(newTransactions);
        allTransactions.add(newTransaction);
        Map<String, Long> minersMap = blockMap.values().stream()
                .collect(Collectors.groupingBy(Block::getMiner, Collectors.counting()));
        Map<String, Long> balanceMap = new HashMap<>();
        minersMap.forEach((miner, minedBlocks) -> balanceMap.put(miner, minedBlocks * 100));
        for (int i = 0; i < allTransactions.size(); i++) {
            Transaction t = allTransactions.get(i);
            boolean isSignatureValid = false;
            try {
                isSignatureValid = GenerateKeys
                        .verifySignature(t.getData().getBytes(), t.getSignature(), t.getFrom().getPublicKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isSignatureValid) {
                return false;
            }
            String from = t.getFrom().getName();
            String to = t.getTo().getName();
            //sender
            if (balanceMap.containsKey(from)) {
                long oldBalance = balanceMap.get(from);
                if (t.getAmount() > oldBalance || t.getAmount() < 0) {
                    return false; // not enough money
                }
                balanceMap.put(from, oldBalance - t.getAmount());
            } else {
                if (t.getAmount() > 100 || t.getAmount() < 0) {
                    return false; // not enough money
                }
                balanceMap.put(from, 100L - t.getAmount());
            }
            //receiver
            if (balanceMap.containsKey(to)) {
                long oldBalance = balanceMap.get(to);
                balanceMap.put(to, oldBalance + t.getAmount());
            } else {
                balanceMap.put(to, 100L + t.getAmount());
            }
            if (i == 0) {
                continue;
            }
            if (t.getId() <= allTransactions.get(i - 1).getId() || t.getId() > lastTransactionId) {
                return false;
            }
        }
        return true;
    }

    public List<Block> getBlocks() {
        return new ArrayList<>(blockMap.values());
    }

    public synchronized int getLastId() {
        return lastId;
    }

    public synchronized String getLastHash() {
        return lastHash;
    }

    public synchronized int getZeros() {
        return zeros;
    }

    public synchronized int getBlocksToMake() {
        return blocksToMake;
    }

    public void setBlocksToMake(int blocksToMake) {
        this.blocksToMake = blocksToMake;
    }

    public void setZeros(int zeros) {
        this.zeros = zeros;
    }

    public synchronized List<Transaction> getTransactionsToBlock() {
        return transactionsToBlock;
    }

}
