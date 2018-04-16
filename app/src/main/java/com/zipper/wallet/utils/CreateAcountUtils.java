package com.zipper.wallet.utils;

import android.content.Context;
import android.util.Log;

import junit.framework.Assert;

import net.bither.bitherj.core.AbstractHD;
import net.bither.bitherj.core.HDAccount;
import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.EncryptedData;
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

/**
 * Created by Administrator on 2018/4/10.
 */
public class CreateAcountUtils {
    static final String TAG = "CreateAcountUtils";
    static Context mContext;
    static ArrayList<String> wordList = new ArrayList<String>(2048);

    /**
     * 实例化MnemoniceCode
     * @param context
     */
    public static void instance(Context context) {

        try {

            try {
                mContext = context;
                MnemonicCode.setInstance(new MnemonicCodeTestClass());//首先实例化助记词类的单例模式

                InputStream stream = mContext.getAssets().open("english.txt");

                BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                wordList = new ArrayList<String>(2048);
                MessageDigest md;
                try {
                    md = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);        // Can't happen.
                }
                String word;
                while ((word = br.readLine()) != null) {
                    md.update(word.getBytes());
                    wordList.add(word);
                }
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShort(mContext,"");
            Log.e("CreateAcountUtils", e + "");
        }
    }



//    public static void createAccountTest(Context context){
//
//        try {
//
//            instance(context);//首先实例化助记词类的单例模式
//
//            byte[] randomSeed = createRandomSeed();//生成128位字节流
//
//
//            List<String> words = getMnemonicCode(randomSeed);//一局随机数获取助记词
//
//            byte[] seed = createMnemSeed(words);//由助记词和密码生成种子,方法内含有转换512哈系数方式
//            Log.i(TAG,"randomSeed :"+Utils.bytesToHexString(randomSeed));
//            Log.i(TAG,"seed :"+Utils.bytesToHexString(MnemonicCode.toSeed(words,"")));
//            Log.i(TAG,"randomSeed :"+Utils.bytesToHexString(MnemonicCode.instance().toEntropy(words)));//利用助记词反推出随机数种子
//
//
//            DeterministicKey master = CreateRootKey(seed);//生成根公私钥对象
//            DeterministicKey accountKey = getAccount(master);
//
//
//            String mnemonicSeed = Utils.bytesToHexString(seed);//助记词生成的根种子
//            String priKey = Utils.bytesToHexString(master.getPrivKeyBytes());//根私钥
//            String pubkey = Utils.bytesToHexString(master.getPubKey());//根公钥
//
//            String firstAddr = getAddress(getAccount(master).deriveSoftened(AbstractHD.PathType.EXTERNAL_ROOT_PATH.getValue()),60);
//
//            EncryptedData encryptedData = new EncryptedData(seed,"abc",false);
//            String encrypt = encryptedData.toEncryptedString();
//            Log.i(TAG,"randomSeed :"+Utils.bytesToHexString(randomSeed));
//            Log.i(TAG,"randomSeed Encrypt:"+encrypt);
//            Log.i(TAG,"randomSeed decrypt:"+Utils.bytesToHexString(new EncryptedData(encrypt).decrypt("abc")));//mnemonic
//            for(String str : words){
//                Log.i(TAG,"words :"+str);
//            }
//            Log.i(TAG,"mnemonicSeed :"+mnemonicSeed);
//            Log.i(TAG,"512PrivateKey:"+priKey);
//            Log.i(TAG,"512publicKey:"+pubkey);
//            Log.i(TAG,"firstAddr:"+firstAddr);
//
//
//            for(String str : MnemonicCode.instance().toMnemonic(MnemonicCode.instance().toEntropy(words))){
//                Log.i(TAG,"words :"+str);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.e(TAG,e+"");
//        }
//
//    }


    /**
     * 生成助记词
     * @param entropy
     * @return
     * @throws MnemonicException
    .MnemonicLengthException
     */
    public static List<String> getMnemonicCode(byte[] entropy) throws MnemonicException
            .MnemonicLengthException {
        if(!isInstanced()){
            Log.e("CreateAcountUtils","doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
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
            Log.e("CreateAcountUtils", index+ " "+wordList.size());
            words.add((String) wordList.get(index));
        }

        return words;

    }


    protected DeterministicKey getChainRootKey(DeterministicKey accountKey, AbstractHD.PathType
            pathType) {
        return accountKey.deriveSoftened(pathType.getValue());
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


    /**
     * 创建随机数种子
     * @return
     */
    public static byte[] createRandomSeed(){
        if(!isInstanced()){
            Log.e("CreateAcountUtils","doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        SecureRandom random = new SecureRandom();//创建随机类的实例
        byte[] randomSeed = new byte[16];//生成128位字节流

        //byte[] radomSeed = random.generateSeed(KeyCrypterScrypt.BLOCK_LENGTH);//生成128位字节流种子
        //Log.i(TAG,"radomSeed :"+ Utils.bytesToHexString(radomSeed));


        random.nextBytes(randomSeed);//生成128位随机数
        return randomSeed;
    }


    public static byte[] createMnemSeed(List<String> words){
        if(!isInstanced()){
            Log.e("CreateAcountUtils","doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }

        return MnemonicCode.toSeed(words,"");//由助记词和密码生成种子,方法内含有转换512哈系数方式

    }


    /**
     * 获取统一地址
     * @param externalKey
     * @return
     */
    public static String getFirstAddress(DeterministicKey externalKey){
        return getAddress(externalKey,0);
    }

    /**
     * 获取指定的地址
     * @param externalKey
     * @param i
     * @return
     */
    public static String getAddress(DeterministicKey externalKey,int i){
        DeterministicKey key = externalKey.deriveSoftened(i);
        return  key.toAddress();
    }

    /**
     * 生成根公私钥对象
     * @param seed
     * @return
     */
    public static DeterministicKey  CreateRootKey(byte[] seed){
        if(!isInstanced()){
            Log.e("CreateAcountUtils","doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }

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
        return master;
    }


    /**
     *  获取内外部钥对象
     * @param master
     * @return
     */
    public static DeterministicKey getAccount(DeterministicKey master,int coin_type) {
        if(!isInstanced()){
            Log.e("CreateAcountUtils","doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        DeterministicKey purpose = master.deriveHardened(44);
        DeterministicKey coinType = purpose.deriveHardened(coin_type);
        DeterministicKey account = coinType.deriveHardened(1);
        DeterministicKey account1 = account.deriveHardened(0);
        purpose.wipe();
        coinType.wipe();
        account.wipe();
        return account1;
    }


    /**
     *  获取内外部钥对象
     * @param master
     * @return
     */
    public static String getWalletAddr(DeterministicKey master,int coin_type) {
        if(!isInstanced()){
            Log.e("CreateAcountUtils","doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        DeterministicKey purpose = master.deriveHardened(44);
        DeterministicKey coinType = purpose.deriveHardened(coin_type);
        DeterministicKey account = coinType.deriveHardened(1);
        DeterministicKey account1 = account.deriveHardened(0);
        purpose.wipe();
        coinType.wipe();
        account.wipe();
        return account1.toAddress1();
    }

    /**
     * 读取词库
     */
    private  static final class MnemonicCodeTestClass extends MnemonicCode {
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


    /**
     * 获取私钥
     * @param seed
     * @param pwd
     * @return
     */
    public List<byte[]> getPrivateKey(byte[] seed , String pwd){
        if(!isInstanced()){
            Log.e("CreateAcountUtils","doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
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





    public static HDAccount createADAccount(byte[] mnemseed,String pwd) throws MnemonicException.MnemonicLengthException {
        if(!isInstanced()){
            Log.e("CreateAcountUtils","doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        return new HDAccount(mnemseed,pwd);
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
            public void addAddress(List<WalletInfo.HDAccountAddress> hdAccountAddresses) {

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
            public WalletInfo.HDAccountAddress addressForPath(int hdAccountId, AbstractHD.PathType type, int index) {
                return null;
            }

            @Override
            public List<byte[]> getPubs(int hdAccountId, AbstractHD.PathType pathType) {
                return null;
            }

            @Override
            public List<WalletInfo.HDAccountAddress> belongAccount(int hdAccountId, List<String> addresses) {
                return null;
            }

            @Override
            public void updateSyncdComplete(int hdAccountId, WalletInfo.HDAccountAddress address) {

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
            public List<WalletInfo.HDAccountAddress> getSigningAddressesForInputs(int hdAccountId, List<In> inList) {
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

        WalletInfo hdAccount = new WalletInfo(seed, new SecureCharSequence(pwd.toCharArray()));
                            *//*WalletInfo hdAccount = new WalletInfo(new SecureRandom(), new SecureCharSequence(pwd.toCharArray()), new WalletInfo.HDAccountGenerationDelegate() {
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
                            new WalletInfo(new EncryptedData(encreyptString)
                                    , password, false)*/
    }


    private static boolean isInstanced(){
        if(mContext == null){
            return false;
        }
        return true;
    }

}
