package com.zipper.wallet.utils;

import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.crypto.hd.HDKeyDerivation;
import net.bither.bitherj.crypto.mnemonic.MnemonicCode;
import net.bither.bitherj.utils.Base58;
import net.bither.bitherj.utils.Utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by AlMn on 2018/04/11.
 */

public class AddrUtils {

    public static String mnemonicWordToAddress(List<String> words) throws NoSuchAlgorithmException {
        byte[] seed = MnemonicCode.toSeed(words, "");

        Utils.bytesToHexString(seed);
        DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);

//        Utils.bytesToHexString(master.getPrivKeyBytes());
//        Utils.bytesToHexString(master.getPubKey());

        return pubKeyToAddress(master.getPubKey());
    }

    public static String ciphertextPrivKeyToAddress(String ciphertext, String password) throws NoSuchAlgorithmException {
        byte[]  bytes=new EncryptedData(ciphertext).decrypt(password);

        return cleartextPrivKeyToAddress(bytes);
    }

    public static String cleartextPrivKeyToAddress(byte[] bytes) throws NoSuchAlgorithmException {
        BigInteger privKey = new BigInteger(1, bytes);
        byte[] pubKeyBytes = ECKey.publicKeyFromPrivate(privKey, true);

        return pubKeyToAddress(pubKeyBytes);
    }

    private static String pubKeyToAddress(byte[] pubKeyBytes) throws NoSuchAlgorithmException {
        byte[] input = Utils.sha256hash160(pubKeyBytes);
        byte[] sha256 = MessageDigest.getInstance("SHA-256").digest(input);
        byte[] result = new byte[25];
        result[0] = 0;
        for (int i = 0; i < input.length; i++) {
            result[i + 1] = input[i];
        }
        result[21] = sha256[0];
        result[22] = sha256[1];
        result[23] = sha256[2];
        result[24] = sha256[3];

        //Base58.hexToBase58WithAddress(Base58.encode(result));
        return Base58.encode(result);
    }


}
