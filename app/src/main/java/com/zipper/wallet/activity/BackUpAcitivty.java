package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.base.CreateActvity;
import com.zipper.wallet.bean.WalletBean;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.RuntListSeria;

import junit.framework.Assert;

import net.bither.bitherj.core.HDAccount;
import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.EncryptedPrivateKey;
import net.bither.bitherj.crypto.KeyCrypterScrypt;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.crypto.hd.HDKeyDerivation;
import net.bither.bitherj.crypto.mnemonic.MnemonicCode;
import net.bither.bitherj.crypto.mnemonic.MnemonicException;
import net.bither.bitherj.utils.Sha256Hash;
import net.bither.bitherj.utils.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public class BackUpAcitivty extends CreateActvity {

    public static ArrayList<String> wordList = new ArrayList<String>(2048);
    Button btnBackup;
    HDAccount hdAccount;

    final int SAVE_PRIVATE = 100;
    final int TRANSMIT_WORDS = 101;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SAVE_PRIVATE:
                    break;

                case TRANSMIT_WORDS:
                    Object obj = msg.obj;
                    if(obj!=null){
                        if(obj instanceof  List){
                            hideProgressDialog();
                            Intent intent = new Intent(mContext,MnemonicActivity.class);
                            intent.putExtra("list",new RuntListSeria<String>((List<String>) obj));
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        toast("未曾生成数据");
                    }

                    break;

            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        btnBackup = (Button)findViewById(R.id.btn_backup);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        },5000);

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog("请输入密码","","", InputType.TYPE_TEXT_VARIATION_PASSWORD,new RuntHTTPApi.ResPonse(){

                    @Override
                    public void doSuccessThing(final Map<String, Object> param) {
                        String pwd = PreferencesUtils.getString(mContext,KEY_WALLET_PWD,PreferencesUtils.VISITOR);
                        if(pwd.equals(param.get(INPUT_TEXT).toString().trim())){
                            alertDialog.dismiss();
                            showProgressDialog("正在导出。。。");
                            new Thread(){
                                @Override
                                public void run() {
                                    createAccount();
                                }
                            }.start();

                        }else{
                            toast("密码错误");
                        }
                    }

                    @Override
                    public void doErrorThing(Map<String, Object> param) {

                    }
                });
            }
        });
        try {

            InputStream stream = mContext.getAssets().open("english.txt");

            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            this.wordList = new ArrayList<String>(2048);
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);        // Can't happen.
            }
            String word;
            while ((word = br.readLine()) != null) {
                md.update(word.getBytes());
                this.wordList.add(word);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void createAccount(){

        try {

            MnemonicCode.setInstance(new MnemonicCodeTestClass());//首先实例化助记词类的单例模式

            SecureRandom random = new SecureRandom();//创建随机类的实例
            byte[] randomSeed = new byte[16];//生成128位字节流

            //byte[] radomSeed = random.generateSeed(KeyCrypterScrypt.BLOCK_LENGTH);//生成128位字节流种子
            //Log.i(TAG,"radomSeed :"+ Utils.bytesToHexString(radomSeed));


            random.nextBytes(randomSeed);//生成128位随机数
            //Log.i(TAG,"mnemonicSeed :"+ Utils.bytesToHexString(mnemonicSeed));//将字节流编译成字符串

            List<String> words = getMnemonicCode(randomSeed);//一局随机数获取助记词

            for(String str : words){
                Log.e(TAG,"words :"+str);
            }

            byte[] seed = MnemonicCode.toSeed(words,"");//由助记词和密码生成种子,方法内含有转换512哈系数方式
            Log.i(TAG,"seed :"+Utils.bytesToHexString(seed));

            /*HMac hMac = HDUtils.createHmacSha512Digest("mnemonic".getBytes());//生成512哈希数
            //byte[] hash512 = HDUtils.hmacSha512(hMac, seed);//生成512哈希数
            byte[] mnemhash512 = HDUtils.hmacSha512(hMac, mnemonicSeed);//生成512哈希数
            //Log.i(TAG,"seedhash512 :"+Utils.bytesToHexString(hash512));
            Log.i(TAG,"mnemonicSeedhash512 :"+Utils.bytesToHexString(mnemhash512));*/
            //List<byte[]> keys = getPrivateKey(hash512,"abcd");

            DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
            String mnemonicSeed = Utils.bytesToHexString(seed);//助记词生成的根种子
            String priKey = Utils.bytesToHexString(master.getPrivKeyBytes());//根私钥
            String pubkey = Utils.bytesToHexString(master.getPubKey());//根公钥
            WalletBean.getWalletBean().setMnemSeed(mnemonicSeed);
            WalletBean.getWalletBean().setRandomSeed(Utils.bytesToHexString(randomSeed));
            WalletBean.getWalletBean().setPriKey(priKey);
            WalletBean.getWalletBean().setPubKey(pubkey);

            //Log.i(TAG,"512PrivateKey:"+priKey);
            //Log.i(TAG,"512publicKey:"+pubkey);
            //Log.i(TAG,"publicKey:"+Utils.bytesToHexString(keys.get(1)));
            //Log.i(TAG,"512publicKey:"+Utils.bytesToHexString(keys512.get(1)));
            Message msg = new Message();
            msg.what = TRANSMIT_WORDS;
            msg.obj = words;
            mHandler.sendMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,e+"");
        }

    }



    public List<String> getMnemonicCode(byte[] entropy) throws MnemonicException
            .MnemonicLengthException {
        if (entropy.length % 4 > 0) {
            throw new MnemonicException.MnemonicLengthException("Entropy length not multiple of "
                    + "32 bits.");
        }

        if (entropy.length == 0) {
            throw new MnemonicException.MnemonicLengthException("Entropy is empty.");
        }

        // We take initial entropy of ENT bits and compute its
        // checksum by taking first ENT / 32 bits of its SHA256 hash.

        byte[] hash = Sha256Hash.create(entropy).getBytes();
        boolean[] hashBits = bytesToBits(hash);

        boolean[] entropyBits = bytesToBits(entropy);
        int checksumLengthBits = entropyBits.length / 32;

        // We append these bits to the end of the initial entropy.
        boolean[] concatBits = new boolean[entropyBits.length + checksumLengthBits];
        System.arraycopy(entropyBits, 0, concatBits, 0, entropyBits.length);
        System.arraycopy(hashBits, 0, concatBits, entropyBits.length, checksumLengthBits);

        // Next we take these concatenated bits and split them into
        // groups of 11 bits. Each group encodes number from 0-2047
        // which is a position in a wordlist.  We convert numbers into
        // words and use joined words as mnemonic sentence.

        ArrayList<String> words = new ArrayList<String>();
        int nwords = concatBits.length / 11;
        for (int i = 0;
             i < nwords;
             ++i) {
            int index = 0;
            for (int j = 0;
                 j < 11;
                 ++j) {
                index <<= 1;
                if (concatBits[(i * 11) + j]) {
                    index |= 0x1;
                }
            }
            words.add((String) this.wordList.get(index));
        }

        return words;

    }

    private static boolean[] bytesToBits(byte[] data) {
        boolean[] bits = new boolean[data.length * 8];
        for (int i = 0;
             i < data.length;
             ++i)
            for (int j = 0;
                 j < 8;
                 ++j)
                bits[(i * 8) + j] = (data[i] & (1 << (7 - j))) != 0;
        return bits;
    }



    public final class MnemonicCodeTestClass extends MnemonicCode {
        private static final String WordListPath = "english.txt";

        public MnemonicCodeTestClass() throws IOException {
            super();
        }

        @Override
        protected InputStream openWordList() throws IOException {
            InputStream stream = mContext.getAssets().open(WordListPath);
            if (stream == null) {
                throw new FileNotFoundException(WordListPath);
            }
            return stream;
        }
    }



    private void nothing(String pwd){


        /*PasswordSeed passwordSeed = new PasswordSeed(pwd);
        boolean reslut = passwordSeed.checkPassword("123456");
        if (reslut) {
            System.out.println("checkPassword pass");
        } else {
            System.out.println("checkPassword no pass");
        }
        AbstractDb.hdAccountProvider = new IHDAccountProvider(){
            @Override
            public int addHDAccount(String encryptedMnemonicSeed, String encryptSeed, String firstAddress, boolean isXrandom, String addressOfPS, byte[] externalPub, byte[] internalPub) {
                return 0;
            }

            @Override
            public int addMonitoredHDAccount(String firstAddress, boolean isXrandom, byte[] externalPub, byte[] internalPub) {
                return 0;
            }

            @Override
            public boolean hasMnemonicSeed(int hdAccountId) {
                return false;
            }

            @Override
            public String getHDFirstAddress(int hdSeedId) {
                return null;
            }

            @Override
            public byte[] getExternalPub(int hdSeedId) {
                return new byte[0];
            }

            @Override
            public byte[] getInternalPub(int hdSeedId) {
                return new byte[0];
            }

            @Override
            public String getHDAccountEncryptSeed(int hdSeedId) {
                return null;
            }

            @Override
            public String getHDAccountEncryptMnemonicSeed(int hdSeedId) {
                return null;
            }

            @Override
            public boolean hdAccountIsXRandom(int seedId) {
                return false;
            }

            @Override
            public List<Integer> getHDAccountSeeds() {
                return null;
            }

            @Override
            public boolean isPubExist(byte[] externalPub, byte[] internalPub) {
                return false;
            }
        };

        AbstractDb.hdAccountAddressProvider = new IHDAccountAddressProvider() {
            @Override
            public void addAddress(List<HDAccount.HDAccountAddress> hdAccountAddresses) {

            }

            @Override
            public int issuedIndex(int hdAccountId, AbstractHD.PathType pathType) {
                return 0;
            }

            @Override
            public int allGeneratedAddressCount(int hdAccountId, AbstractHD.PathType pathType) {
                return 0;
            }

            @Override
            public void updateIssuedIndex(int hdAccountId, AbstractHD.PathType pathType, int index) {

            }

            @Override
            public String externalAddress(int hdAccountId) {
                return null;
            }

            @Override
            public HashSet<String> getBelongAccountAddresses(int hdAccountId, List<String> addressList) {
                return null;
            }

            @Override
            public HashSet<String> getBelongAccountAddresses(List<String> addressList) {
                return null;
            }

            @Override
            public Tx updateOutHDAccountId(Tx tx) {
                return null;
            }

            @Override
            public int getRelatedAddressCnt(List<String> addresses) {
                return 0;
            }

            @Override
            public List<Integer> getRelatedHDAccountIdList(List<String> addresses) {
                return null;
            }

            @Override
            public HDAccount.HDAccountAddress addressForPath(int hdAccountId, AbstractHD.PathType type, int index) {
                return null;
            }

            @Override
            public List<byte[]> getPubs(int hdAccountId, AbstractHD.PathType pathType) {
                return null;
            }

            @Override
            public List<HDAccount.HDAccountAddress> belongAccount(int hdAccountId, List<String> addresses) {
                return null;
            }

            @Override
            public void updateSyncdComplete(int hdAccountId, HDAccount.HDAccountAddress address) {

            }

            @Override
            public void setSyncedNotComplete() {

            }

            @Override
            public int unSyncedAddressCount(int hdAccountId) {
                return 0;
            }

            @Override
            public void updateSyncedForIndex(int hdAccountId, AbstractHD.PathType pathType, int index) {

            }

            @Override
            public List<HDAccount.HDAccountAddress> getSigningAddressesForInputs(int hdAccountId, List<In> inList) {
                return null;
            }

            @Override
            public int hdAccountTxCount(int hdAccountId) {
                return 0;
            }

            @Override
            public long getHDAccountConfirmedBalance(int hdAccountId) {
                return 0;
            }

            @Override
            public List<Tx> getHDAccountUnconfirmedTx(int hdAccountId) {
                return null;
            }

            @Override
            public long sentFromAccount(int hdAccountId, byte[] txHash) {
                return 0;
            }

            @Override
            public List<Tx> getTxAndDetailByHDAccount(int hdAccountId, int page) {
                return null;
            }

            @Override
            public List<Tx> getTxAndDetailByHDAccount(int hdAccountId) {
                return null;
            }

            @Override
            public List<Out> getUnspendOutByHDAccount(int hdAccountId) {
                return null;
            }

            @Override
            public List<Tx> getRecentlyTxsByAccount(int hdAccountId, int greaterThanBlockNo, int limit) {
                return null;
            }

            @Override
            public int getUnspendOutCountByHDAccountWithPath(int hdAccountId, AbstractHD.PathType pathType) {
                return 0;
            }

            @Override
            public List<Out> getUnspendOutByHDAccountWithPath(int hdAccountId, AbstractHD.PathType pathType) {
                return null;
            }

            @Override
            public int getUnconfirmedSpentOutCountByHDAccountWithPath(int hdAccountId, AbstractHD.PathType pathType) {
                return 0;
            }

            @Override
            public List<Out> getUnconfirmedSpentOutByHDAccountWithPath(int hdAccountId, AbstractHD.PathType pathType) {
                return null;
            }

            @Override
            public boolean requestNewReceivingAddress(int hdAccountId) {
                return false;
            }
        };

        HDAccount hdAccount = new HDAccount(seed, new SecureCharSequence(pwd.toCharArray()));
                            *//*HDAccount hdAccount = new HDAccount(new SecureRandom(), new SecureCharSequence(pwd.toCharArray()), new HDAccount.HDAccountGenerationDelegate() {
                                @Override
                                public void onHDAccountGenerationProgress(double progress) {
                                    Log.i(TAG,"progress :"+progress);
                                }
                            });*//*
        hdAccount.getPubKey();
        Log.i(TAG,"hdAccount :"+ Base58.decode(pwd));
                            *//*AbstractDb.construct();
                            List<Integer> seeds = AbstractDb.hdAccountProvider.getHDAccountSeeds();
                            for (int seedId : seeds) {
                                Log.i(TAG,"seedId :"+seedId);
                            }
                            new HDAccount(new EncryptedData(encreyptString)
                                    , password, false)*/
    }

    public List<byte[]> getPrivateKey(byte[] seed , String pwd){
        SecureRandom random = new SecureRandom();//创建随机类的实例
        //seed = Utils.hexStringToByteArray("87ce7991df7641153ca180e944a2414c5342df2ffbe8d6408a72bc8732aef6b6");

        byte[] radomSeed = random.generateSeed(KeyCrypterScrypt.SALT_LENGTH);//生成
        KeyCrypterScrypt keyCrypter = new KeyCrypterScrypt(radomSeed);
        // Encrypt.
        EncryptedPrivateKey encryptedPrivateKey = keyCrypter.encrypt(seed, keyCrypter.deriveKey(pwd));
        Assert.assertNotNull(encryptedPrivateKey);
        // Decrypt.
        //byte[] reborn = keyCrypter.decrypt(encryptedPrivateKey, keyCrypter.deriveKey(pwd));//进行解密
        // Decrypt.

        BigInteger bigInteger = new BigInteger(1, keyCrypter.decrypt(encryptedPrivateKey, keyCrypter.deriveKey(pwd)));
        byte[] privateKey = encryptedPrivateKey.getEncryptedBytes();
        byte[] publicKey = ECKey.publicKeyFromPrivate(bigInteger, true);//依据私钥获取公钥
        List<byte[]> list = new ArrayList<>();
        list.add(privateKey);
        list.add(publicKey);
        return  list;
    }


}
