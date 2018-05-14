package com.zipper.wallet.btc;

import java.io.IOException;
import java.io.OutputStream;
import net.bither.bitherj.utils.Utils;
import net.bither.bitherj.utils.VarInt;

public class In {
    public static final long NO_SEQUENCE = 0xFFFFFFFFL;
    private byte[] prevTxHash;
    private int prevOutSn;
    private byte[] inSignature;
    private long inSequence;
    private byte[] prevOutScript;

    public byte[] getPrevTxHash() {
        return prevTxHash;
    }

    public void setPrevTxHash(byte[] prevTxHash) {
        this.prevTxHash = prevTxHash;
    }

    public int getPrevOutSn() {
        return prevOutSn;
    }

    public void setPrevOutSn(int prevOutSn) {
        this.prevOutSn = prevOutSn;
    }

    public byte[] getInSignature() {
        return inSignature;
    }

    public void setInSignature(byte[] inSignature) {
        this.inSignature = inSignature;
    }

    public long getInSequence() {
        return inSequence;
    }

    public void setInSequence(long inSequence) {
        this.inSequence = inSequence;
    }

    public boolean hasSequence() {
        return inSequence != NO_SEQUENCE;
    }

    public byte[] getPrevOutScript() {
        return prevOutScript;
    }

    public void setPrevOutScript(byte[] prevOutScript) {
        this.prevOutScript = prevOutScript;
    }

    public In() {
        this.inSequence = NO_SEQUENCE;
    }

    public In(byte[] prevTxHash, int prevOutSn, byte[] prevOutScript) {
        this.prevTxHash = prevTxHash;
        this.prevOutSn = prevOutSn;
        this.prevOutScript = prevOutScript;
        this.inSequence = NO_SEQUENCE;
    }

    public void bitcoinSerializeToStream(OutputStream stream) throws IOException {
        stream.write(prevTxHash);
        Utils.uint32ToByteStreamLE(prevOutSn, stream);
        stream.write(new VarInt(inSignature.length).encode());
        stream.write(inSignature);
        Utils.uint32ToByteStreamLE(inSequence, stream);
    }
}
