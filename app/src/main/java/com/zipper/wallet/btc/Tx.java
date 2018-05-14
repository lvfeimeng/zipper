package com.zipper.wallet.btc;
import net.bither.bitherj.utils.Utils;
import net.bither.bitherj.utils.VarInt;
import net.bither.bitherj.utils.UnsafeByteArrayOutputStream;
import net.bither.bitherj.script.Script;
import net.bither.bitherj.script.ScriptOpCodes;
import static net.bither.bitherj.utils.Utils.uint32ToByteStreamLE;
import static net.bither.bitherj.utils.Utils.doubleDigest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Tx  {
    /**
     * How many bytes a transaction can be before it won't be relayed anymore. Currently 100kb.
     */
    public static final int MAX_STANDARD_TX_SIZE = 100 * 1024;
    /**
     * If fee is lower than this value (in satoshis), a default reference client will treat it as
     * if there were no fee.
     * Currently this is 10000 satoshis.
     */
    public static final long REFERENCE_DEFAULT_MIN_TX_FEE = 1000;
    /**
     * Any standard (ie pay-to-address) output smaller than this value (in satoshis) will most
     * likely be rejected by the network.
     * This is calculated by assuming a standard output will be 34 bytes,
     * and then using the formula used in
     */
    public static final long MIN_NONDUST_OUTPUT = 5460;
    public static final long TX_VERSION = 1l;
    public static final long TX_LOCKTIME = 0l;


    private long txVer;
    private long txLockTime;
    private List<In> ins;
    private List<Out> outs;

    public long getTxVer() {
        return txVer;
    }

    public void setTxVer(long txVer) {
        this.txVer = txVer;
    }

    public long getTxLockTime() {
        return txLockTime;
    }

    public void setTxLockTime(long txLockTime) {
        this.txLockTime = txLockTime;
    }

    public List<In> getIns() {
        return ins;
    }

    public void setIns(List<In> ins) {
        this.ins = ins;
    }

    public List<Out> getOuts() {
        return outs;
    }

    public void setOuts(List<Out> outs) {
        this.outs = outs;
    }

    public Tx() {
        this.ins = new ArrayList<In>();
        this.outs = new ArrayList<Out>();
        this.txVer = TX_VERSION;
        this.txLockTime = TX_LOCKTIME;
    }

    /**
     * Removes all the inputs from this transaction.
     * Note that this also invalidates the length attribute
     */
    public void clearInputs() {
        ins.clear();
    }

    /**
     * Adds an input directly, with no checking that it's valid. Returns the new input.
     */
    public In addInput(In input) {
        ins.add(input);
        return input;
    }

    /**
     * Removes all the inputs from this transaction.
     * Note that this also invalidates the length attribute
     */
    public void clearOutputs() {
        outs.clear();
    }

    /**
     * Adds the given output to this transaction. The output must be completely initialized.
     * Returns the given output.
     */
    public Out addOutput(Out to) {
        outs.add(to);
        return to;
    }

    protected void bitcoinSerializeToStream(OutputStream stream) throws IOException {
        uint32ToByteStreamLE(txVer, stream);
        stream.write(new VarInt(ins.size()).encode());
        for (In in : ins)
            in.bitcoinSerializeToStream(stream);
        stream.write(new VarInt(outs.size()).encode());
        for (Out out : outs)
            out.bitcoinSerializeToStream(stream);
        uint32ToByteStreamLE(txLockTime, stream);
    }

    public byte[] bitcoinSerialize() {
        // No cached array available so serialize parts by stream.
        ByteArrayOutputStream stream = new UnsafeByteArrayOutputStream(estimateSize());
        try {
            bitcoinSerializeToStream(stream);
        } catch (IOException e) {
            // Cannot happen, we are serializing to a memory stream.
        }

        // Record length. If this Message wasn't parsed from a byte stream it won't have length field
        // set (except for static length message types).  Setting it makes future streaming more efficient
        // because we can preallocate the ByteArrayOutputStream buffer and avoid resizing.
        return stream.toByteArray();
    }

    public boolean isTimeLocked() {
        if (getTxLockTime() == 0) {
            return false;
        }
        for (In input : getIns())
            if (input.hasSequence()) {
                return true;
            }
        return false;
    }

    public boolean isSigned() {
        boolean isSign = this.getIns().size() > 0;
        for (In in : this.getIns()) {
            isSign &= in.getInSignature() != null && in.getInSignature().length > 0;
        }
        return isSign;
    }

    public boolean hasDustOut() {
        for (Out out : this.getOuts()) {
            if (out.getOutValue() <= Tx.MIN_NONDUST_OUTPUT) {
                return true;
            }
        }
        return false;
    }

    int estimateSize() {
        int length = 0;

        length += VarInt.sizeOf(txVer);

        long txInCount = ins.size();
        length += VarInt.sizeOf(txInCount);
        for (int i = 0;i < txInCount;i++) {
            byte[] scriptBytes = ins.get(i).getInSignature();
            length += 40 + (scriptBytes == null ? 1 : VarInt.sizeOf(scriptBytes.length) + scriptBytes.length);
        }

        long txOutCount = outs.size();
        length += VarInt.sizeOf(txOutCount);
        for (int i = 0;i < txOutCount;i++) {
            byte[] scriptBytes = outs.get(i).getOutScript();
            length += 8 + VarInt.sizeOf(scriptBytes.length) + scriptBytes.length;
        }

        length += VarInt.sizeOf(txLockTime);
        return length;
    }


    public List<byte[]> getUnsignedInHashes(byte sigHashType) {
        List<byte[]> result = new ArrayList<byte[]>();
        int i = 0;
        for (In in : this.getIns()) {
            result.add(this.hashForSignature(i, in.getPrevOutScript(), sigHashType));
            i++;
        }
        return result;
    }

    public void signWithSignatures(List<byte[]> signatures) {
        int i = 0;
        for (In in : this.getIns()) {
            in.setInSignature(signatures.get(i));
            i++;
        }
    }

    /**
     * This is required for signatures which use a sigHashType which cannot be represented using
     * SigHash and anyoneCanPay
     * See transaction c99c49da4c38af669dea436d3e73780dfdb6c1ecf9958baa52960e8baee30e73,
     * which has sigHashType 0
     */
    public byte[] hashForSignature(int inputIndex, byte[] connectedScript,
                                                byte sigHashType) {
        // The SIGHASH flags are used in the design of contracts, please see this page for a
        // further understanding of
        // the purposes of the code in this method:
        //
        //   https://en.bitcoin.it/wiki/Contracts

        try {
            // Store all the input scripts and clear them in preparation for signing. If we're
            // signing a fresh
            // transaction that step isn't very helpful, but it doesn't add much cost relative to
            // the actual
            // EC math so we'll do it anyway.
            //
            // Also store the input sequence numbers in case we are clearing them with SigHash
            // .NONE/SINGLE
            byte[][] inputScripts = new byte[this.getIns().size()][];
            long[] inputSequenceNumbers = new long[this.getIns().size()];
            for (int i = 0;i < this.getIns().size();i++) {
                inputScripts[i] = this.getIns().get(i).getInSignature();
                inputSequenceNumbers[i] = this.getIns().get(i).getInSequence();
                this.getIns().get(i).setInSignature(new byte[0]);
            }

            // This step has no purpose beyond being synchronized with the reference clients bugs
            // . OP_CODESEPARATOR
            // is a legacy holdover from a previous, broken design of executing scripts that
            // shipped in Bitcoin 0.1.
            // It was seriously flawed and would have let anyone take anyone elses money. Later
            // versions switched to
            // the design we use today where scripts are executed independently but share a stack
            // . This left the
            // OP_CODESEPARATOR instruction having no purpose as it was only meant to be used
            // internally, not actually
            // ever put into scripts. Deleting OP_CODESEPARATOR is a step that should never be
            // required but if we don't
            // do it, we could split off the main chain.
            connectedScript = Script.removeAllInstancesOfOp(connectedScript,
                    ScriptOpCodes.OP_CODESEPARATOR);

            // Set the input to the script of its output. Satoshi does this but the step has no
            // obvious purpose as
            // the signature covers the hash of the prevout transaction which obviously includes
            // the output script
            // already. Perhaps it felt safer to him in some way, or is another leftover from how
            // the code was written.
            In input = this.getIns().get(inputIndex);
            input.setInSignature(connectedScript);

            List<Out> outputs = this.getOuts();
            if ((sigHashType & 0x1f) == (TransactionSignature.SigHash.NONE.ordinal() + 1)) {
                // SIGHASH_NONE means no outputs are signed at all - the signature is effectively
                // for a "blank cheque".
                this.outs = new ArrayList<Out>(0);
                // The signature isn't broken by new versions of the transaction issued by other
                // parties.
                for (int i = 0;i < this.getIns().size();i++)
                    if (i != inputIndex) {
                        this.getIns().get(i).setInSequence(0);
                    }
            } else if ((sigHashType & 0x1f) == (TransactionSignature.SigHash.SINGLE.ordinal() + 1)) {
                // SIGHASH_SINGLE means only sign the output at the same index as the input (ie,
                // my output).
                if (inputIndex >= this.getOuts().size()) {
                    // The input index is beyond the number of outputs,
                    // it's a buggy signature made by a broken
                    // Bitcoin implementation. The reference client also contains a bug in
                    // handling this case:
                    // any transaction output that is signed in this case will result in both the
                    // signed output
                    // and any future outputs to this public key being steal-able by anyone who has
                    // the resulting signature and the public key (both of which are part of the
                    // signed tx input).
                    // Put the transaction back to how we found it.
                    //
                    // Satoshis bug is that SignatureHash was supposed to return a hash and on
                    // this codepath it
                    // actually returns the constant "1" to indicate an error,
                    // which is never checked for. Oops.
                    return Utils.hexStringToByteArray("0100000000000000000000000000000000000000000000000000000000000000");
                }
                // In SIGHASH_SINGLE the outputs after the matching input index are deleted,
                // and the outputs before
                // that position are "nulled out". Unintuitively,
                // the value in a "null" transaction is set to -1.
                this.outs = new ArrayList<Out>(this.getOuts().subList(0, inputIndex + 1));
                for (int i = 0; i < inputIndex; i++)
                    this.getOuts().set(i, new Out( -1, new byte[]{}));
                // The signature isn't broken by new versions of the transaction issued by other
                // parties.
                for (int i = 0;i < this.getIns().size(); i++)
                    if (i != inputIndex) {
                        this.getIns().get(i).setInSequence(0);
                    }
            }

            List<In> inputs = this.getIns();
            if ((sigHashType & TransactionSignature.SIGHASH_ANYONECANPAY_VALUE) ==
                    TransactionSignature.SIGHASH_ANYONECANPAY_VALUE) {
                // SIGHASH_ANYONECANPAY means the signature in the input is not broken by
                // changes/additions/removals
                // of other inputs. For example, this is useful for building assurance contracts.
                this.ins = new ArrayList<In>();
                this.getIns().add(input);
            }

            ByteArrayOutputStream bos = new UnsafeByteArrayOutputStream( 256);
            bitcoinSerializeToStream(bos);
            // We also have to write a hash type (sigHashType is actually an unsigned char)
            uint32ToByteStreamLE(0x000000ff & sigHashType, bos);
            // Note that this is NOT reversed to ensure it will be signed correctly. If it were
            // to be printed out
            // however then we would expect that it is IS reversed.
            byte[] hash = doubleDigest(bos.toByteArray());
            bos.close();

            // Put the transaction back to how we found it.
            this.ins = inputs;
            for (int i = 0; i < inputs.size();i++) {
                inputs.get(i).setInSignature(inputScripts[i]);
                inputs.get(i).setInSequence(inputSequenceNumbers[i]);
            }
            this.outs = outputs;
            return hash;
        } catch (IOException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }
}
