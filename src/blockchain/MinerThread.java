package blockchain;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class MinerThread extends Thread {
    private BlockChain blockChain;
    private String name;
    public VirtualCoinClient client;

    public MinerThread(BlockChain blockChain, String name) {
        this.blockChain = blockChain;
        this.name = name;
        client = new VirtualCoinClient(name);
    }

    @Override
    public void run() {
        while (blockChain.getBlocksToMake() > 0) {
            int lastId = blockChain.getLastId();
            String lastHash = blockChain.getLastHash();
            int zeros = blockChain.getZeros();
            List<Transaction> blockData = blockChain.getTransactionsToBlock();
            Block block = new Block(lastId + 1, lastHash, zeros, name, blockData);

            Random random = new Random(new Date().getTime());
            block.proofOfWork = random.nextInt();
            long start = new Date().getTime();
            while (!block.getHash().startsWith("0".repeat(zeros)) && lastId == blockChain.getLastId()) {
                block.proofOfWork = random.nextInt();
            }
            block.setCreationTimeSeconds((new Date().getTime() - start) / 1000);
            blockChain.addBlockToChain(block);
        }
    }
}

