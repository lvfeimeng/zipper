package com.zipper.wallet.btc;

import  net.bither.bitherj.utils.Utils;
import  net.bither.bitherj.utils.VarInt;

import java.io.IOException;
import java.io.OutputStream;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Out {
    private byte[] txHash;
    private int outSn;
    private byte[] outScript;
    private long outValue;

    public byte[] getTxHash() {
        return txHash;
    }

    public void setTxHash(byte[] txHash) {
        this.txHash = txHash;
    }

    public int getOutSn() {
        return outSn;
    }

    public void setOutSn(int outSn) {
        this.outSn = outSn;
    }

    public byte[] getOutScript() {
        return outScript;
    }

    public void setOutScript(byte[] outScript) {
        this.outScript = outScript;
    }

    public long getOutValue() {
        return outValue;
    }

    public void setOutValue(long outValue) {
        this.outValue = outValue;
    }

    public Out(byte[] txHash, int outSn, byte[] outScript, long outValue) {
        this.txHash = txHash;
        this.outSn = outSn;
        this.outScript = outScript;
        this.outValue = outValue;
    }

    public Out(long value, byte[] scriptBytes) {
        checkArgument(value >= 0 || value == -1, "Negative values not allowed");
        this.outValue = value;
        this.outScript = scriptBytes;
    }

    public void bitcoinSerializeToStream(OutputStream stream) throws IOException {
        checkNotNull(outScript);
        Utils.int64ToByteStreamLE(outValue, stream);
        // TODO: Move script serialization into the Script class, where it belongs.
        stream.write(new VarInt(outScript.length).encode());
        stream.write(outScript);
    }
}
