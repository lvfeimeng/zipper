package com.zipper.wallet.ether;


import org.bouncycastle.jcajce.provider.digest.Keccak;

public class KECCAK256 {

    public static byte[] keccak256(byte[] bytes) {
        return keccak256(bytes, 0, bytes.length);
    }

    public static byte[] keccak256(byte[] bytes, int offset, int size) {
        Keccak.DigestKeccak kecc = new Keccak.Digest256();
        kecc.update(bytes, offset, size);
        return kecc.digest();
    }
}