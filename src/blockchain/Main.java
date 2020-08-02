package blockchain;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        Map<Integer, Block> blockMap = null;
        try {
            blockMap = (Map<Integer, Block>) SerializationUtils.deserialize("chain.txt");
            System.out.println("Blockchain loaded from file");
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("New blockchain will be created");
        }
        BlockChain chain = blockMap == null || blockMap.size() == 0 ? new BlockChain() : new BlockChain(blockMap);

        System.out.print("How many block should be create: ");
        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
        chain.setBlocksToMake(count);

        int minerCount = 10;

        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        executor.submit(new ClientThread("miner1", chain));

        int numberOfTasks = minerCount;
        for (int i = 0; i < numberOfTasks; i++) {
            executor.submit(new MinerThread(chain, "miner" + (i + 1)));
        }
        executor.shutdown();
        executor.awaitTermination(200, TimeUnit.SECONDS);
    }

}
