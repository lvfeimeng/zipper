package com.zipper.wallet.ether;

import com.zipper.wallet.utils.ether.KECCAK256;

import java.util.Arrays;

import net.bither.bitherj.crypto.ECKey;

/**
 * <p>Transaction signing logic.</p>
 *
 * <p>Adapted from the
 * <a href="https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/ECKey.java">
 * BitcoinJ ECKey</a> implementation.
 */
public class SignEther {

    public static SignatureData signMessage(byte[] message, ECKey keyPair) {
        return signMessage(message, keyPair, true);
    }

    public static SignatureData signMessage(byte[] message, ECKey keyPair, boolean needToHash) {
        byte[] messageHash;
        if (needToHash) {
            messageHash = KECCAK256.keccak256(message);
        } else {
            messageHash = message;
        }

        byte[] sigData = keyPair.signHash(messageHash, null);
        if (!keyPair.verify(messageHash,sigData)) {
            throw new RuntimeException("sign error, verify failed");
        }

        return new SignatureData((byte)(sigData[0]-27), Arrays.copyOfRange(sigData, 1, 32), Arrays.copyOfRange(sigData, 32, 32));
    }


    public static class SignatureData {
        private final byte v;
        private final byte[] r;
        private final byte[] s;

        public SignatureData(byte v, byte[] r, byte[] s) {
            this.v = v;
            this.r = r;
            this.s = s;
        }

        public byte getV() {
            return v;
        }

        public byte[] getR() {
            return r;
        }

        public byte[] getS() {
            return s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SignatureData that = (SignatureData) o;

            if (v != that.v) {
                return false;
            }
            if (!Arrays.equals(r, that.r)) {
                return false;
            }
            return Arrays.equals(s, that.s);
        }

        @Override
        public int hashCode() {
            int result = (int) v;
            result = 31 * result + Arrays.hashCode(r);
            result = 31 * result + Arrays.hashCode(s);
            return result;
        }
    }
}