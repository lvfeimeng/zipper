package com.zipper.wallet;

import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.ether.EtherRawTransaction;
import com.zipper.wallet.utils.CreateAcountUtils;

import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.crypto.hd.HDKeyDerivation;
import net.bither.bitherj.utils.Utils;

import org.litepal.crud.DataSupport;

import java.math.BigInteger;
import java.security.SignatureException;

public class Test {

    private void test() {
        try {
            BigInteger nonce = new BigInteger("1");
            BigInteger gasPrice = new BigInteger("0");
            BigInteger gasLimit = new BigInteger("21000");
            String to = "0x192d4da9...";
            BigInteger value = new BigInteger("10000000");

            EtherRawTransaction eth = EtherRawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, null);
            WalletInfo walletInfo = DataSupport.find(WalletInfo.class, 0);
            String pwd = "";
            byte[] seed = new EncryptedData(walletInfo.getEsda_seed()).decrypt(pwd);
            DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
            byte[] bytes = eth.Sign(master);
            String sendData = Utils.bytesToHexString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
