package blockchain;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 94L;

    private int id;
    private final VirtualCoinClient from;
    private final VirtualCoinClient to;
    private final int amount;
    private byte[] signature;
    private final PublicKey publicKey;

    public Transaction(VirtualCoinClient from, VirtualCoinClient to, int amount, PrivateKey privateKey, PublicKey publicKey) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.publicKey = publicKey;
        try {
            this.signature = GenerateKeys.sign(getData(), privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getData() {
        return "" + id + from + to + amount;
    }

    public int getId() {
        return id;
    }

    public VirtualCoinClient getFrom() {
        return from;
    }

    public VirtualCoinClient getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return from.getName() + " sent " + amount + " to " + to.getName();
    }
}
