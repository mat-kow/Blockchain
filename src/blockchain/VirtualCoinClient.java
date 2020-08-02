package blockchain;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class VirtualCoinClient implements Serializable {
    private static final long serialVersionUID = 94L;

    private final String name;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public VirtualCoinClient(String name) {
        this.name = name;
        setKeys(name);
    }

    private void setKeys(String name) {
        String publicFileName = "keys/" + name + "Public";
        String privateFileName = "keys/" + name + "Private";
        try {
            publicKey = GenerateKeys.getPublic(publicFileName);
            privateKey = GenerateKeys.getPrivate(privateFileName);
        } catch (Exception e) {
            try {
                GenerateKeys gk = new GenerateKeys(512);
                gk.createKeys();
                publicKey = gk.getPublicKey();
                gk.writeToFile(publicFileName, publicKey.getEncoded());
                privateKey = gk.getPrivateKey();
                gk.writeToFile(privateFileName, privateKey.getEncoded());
            } catch (NoSuchAlgorithmException | NoSuchProviderException | IOException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}