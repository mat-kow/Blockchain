package blockchain;

public class ClientThread extends Thread {
    private final VirtualCoinClient client;
    private final BlockChain blockChain;

    public ClientThread(String name, BlockChain blockChain) {
        this.blockChain = blockChain;
        client = new VirtualCoinClient(name);
    }
    @Override
    public void run() {
        VirtualCoinClient carWash = new VirtualCoinClient("Car wash");
        int count = 10;
        while (count-- > 0) {
            Transaction transaction = new Transaction(client, carWash, 100, client.getPrivateKey(), client.getPublicKey());
            blockChain.addTransaction(transaction);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
