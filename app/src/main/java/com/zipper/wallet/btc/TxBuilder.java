package com.zipper.wallet.btc;

import com.zipper.wallet.utils.MyLog;

import net.bither.bitherj.BitherjSettings;
import net.bither.bitherj.exception.TxBuilderException;
import net.bither.bitherj.script.Script;
import net.bither.bitherj.script.ScriptBuilder;

import java.util.List;

public class TxBuilder {
    protected static long TX_FREE_MIN_PRIORITY = 57600000l;

    public TxBuilder() {

    }

    public Tx buildTxFromAllAddress(List<Out> unspendOuts, List<Long> amounts, List<String> addresses) throws TxBuilderException {
        long value = 0;
        for (long amount : amounts) {
//            if (amount < Tx.MIN_NONDUST_OUTPUT) {
//                throw new TxBuilderException(TxBuilderException.ERR_TX_DUST_OUT_CODE);
//            }
            value += amount;
        }

        if (value > getAmount(unspendOuts)) {
            throw new TxBuilderException.TxBuilderNotEnoughMoneyException(value - TxBuilder.getAmount(unspendOuts));
        }

//        if (value != getAmount(unspendOuts)) {
//            return null;
//        }


        Tx tx = new Tx();
        for (int i = 0; i < amounts.size(); i++) {
            try {
                tx.addOutput(new Out(amounts.get(i), ScriptBuilder.createOutputScript(addresses.get(i)).getProgram()));
            }catch (NullPointerException e){
                e.printStackTrace();
                MyLog.e("NullPointerException","i:"+i+" amounts:"+amounts.get(i) );
            }
        }

        for (Out out : tx.getOuts()) {
            if (out.getOutScript().length == 0) {
                return null;
            }
        }

        for (Out out : unspendOuts) {
            tx.addInput(new In(out.getTxHash(), out.getOutSn(), out.getOutScript()));
        }

        if (estimationTxSize(tx.getIns().size(),
                tx.getOuts().size()) > BitherjSettings.MAX_TX_SIZE) {
            throw new TxBuilderException(TxBuilderException.ERR_REACH_MAX_TX_SIZE_LIMIT_CODE);
        }

        return tx;

    }

    public static int estimationTxSize(int inCount, int outCount) {
        return 10 + 149 * inCount + 34 * outCount;
    }

    static int estimationTxSize(int inCount, Script scriptPubKey, List<Out> outs, boolean isCompressed) {
        int size = 8 + 2;

        Script redeemScript = null;
        if (scriptPubKey.isMultiSigRedeem()) {
            redeemScript = scriptPubKey;
            scriptPubKey = ScriptBuilder.createP2SHOutputScript(redeemScript);
        }

        int sigScriptSize = scriptPubKey.getNumberOfBytesRequiredToSpend(isCompressed, redeemScript);
        size += inCount * (32 + 4 + 1 + sigScriptSize + 4);

        for (Out out : outs) {
            size += 8 + 1 + out.getOutScript().length;
        }
        return size;
    }

    static long getAmount(List<Out> outs) {
        long amount = 0;
        for (Out out : outs) {
            amount += out.getOutValue();
        }
        return amount;
    }
}