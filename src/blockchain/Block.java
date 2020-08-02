package blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Block implements Serializable {
    //noy in hash
    private static final long serialVersionUID = 96L;
    private long creationTimeSeconds;
    private final int myN;
    private int nextN;

    //hash
    private String miner;
    private int id;
    private String previousHash;
    private long timeStamp;
    public int proofOfWork;
    private List<Transaction> blockData;

    public Block(int id, String previousHash, int zeros, String miner, List<Transaction> blockData) {
        this.id = id;
        this.previousHash = previousHash;
        this.miner = miner;
        timeStamp = new Date().getTime();
        myN = zeros;
        this.blockData = blockData;
//        generateProofOfWork(zeros);
    }

    public String getHash() {
        return HashSha256.applySha256(id + previousHash + timeStamp + proofOfWork + blockData + miner);
    }

    @Override
    public String toString() {
        String nChange = myN == nextN ? "N stays the same" :
                (myN > nextN ? "N was decreased by 1" : "N was increased to " + nextN);
        StringBuilder messages = new StringBuilder();
        if (blockData == null || blockData.size() == 0) {
            messages.append("no transactions\n");
        } else {
            blockData.forEach(m -> messages.append(m).append("\n"));
        }
        return "Block:" +
                "\nCreated by " + miner +
                "\n" + miner + " gets 100 VC" +
                "\nId: " + id +
                "\nTimestamp: " + timeStamp +
                "\nMagic number: " + proofOfWork +
                "\nHash of the previous block:\n" + previousHash +
                "\nHash of the block:\n" + getHash() +
                "\nBlock data:\n" + messages +
                "Block was generating for " + creationTimeSeconds + " seconds" +
                "\n" + nChange;
    }

    public int getId() {
        return id;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setNextN(int nextN) {
        this.nextN = nextN;
    }

//    private void generateProofOfWork (int zeros) {
////        proofOfWork = 0;
////        while (!getHash().startsWith("0".repeat(zeros))) {
////            proofOfWork++;
////        }
//        Random random = new Random(new Date().getTime());
//        proofOfWork = random.nextInt();
//        long start = new Date().getTime();
//        while (!getHash().startsWith("0".repeat(zeros))) {
//            proofOfWork = random.nextInt();
//        }
//        creationTimeSeconds = (new Date().getTime() - start) / 1000;
//    }

    public long getCreationTimeSeconds() {
        return creationTimeSeconds;
    }

    public void setCreationTimeSeconds(long creationTimeSeconds) {
        this.creationTimeSeconds = creationTimeSeconds;
    }

    public List<Transaction> getBlockData() {
        return blockData;
    }

    public String getMiner() {
        return miner;
    }

    public int getNextN() {
        return nextN;
    }
}
