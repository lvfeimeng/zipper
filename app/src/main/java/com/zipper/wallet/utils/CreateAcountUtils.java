package com.zipper.wallet.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.WalletInfo;

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
import net.bither.bitherj.utils.Utils;

import org.litepal.crud.DataSupport;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/10.
 */
public class CreateAcountUtils {
    static final String TAG = "CreateAcountUtils";
    static Context mContext;
    static ArrayList<String> wordList = new ArrayList<String>(2048);

    /**
     * 实例化MnemoniceCode
     *
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
            ToastUtils.showShort(mContext, "");
            MyLog.e("CreateAcountUtils", e + "");
        }
    }


    public static void createAccountTest(Context context) {

        try {

            instance(context);//首先实例化助记词类的单例模式

            byte[] randomSeed = createRandomSeed();//生成128位字节流


            List<String> words = getMnemonicCode(randomSeed);//一局随机数获取助记词

            byte[] seed = createMnemSeed(words);//由助记词和密码生成种子,方法内含有转换512哈系数方式
            MyLog.i(TAG, "randomSeed :" + Utils.bytesToHexString(randomSeed));
            MyLog.i(TAG, "seed :" + Utils.bytesToHexString(MnemonicCode.toSeed(words, "")));
            MyLog.i(TAG, "randomSeed :" + Utils.bytesToHexString(MnemonicCode.instance().toEntropy(words)));//利用助记词反推出随机数种子


            DeterministicKey master = CreateRootKey(seed);//生成根公私钥对象
            //DeterministicKey accountKey = getAccount(master);


            String mnemonicSeed = Utils.bytesToHexString(seed);//助记词生成的根种子
            String priKey = Utils.bytesToHexString(master.getPrivKeyBytes());//根私钥
            String pubkey = Utils.bytesToHexString(master.getPubKey());//根公钥

            //String firstAddr = getAddress(getAccount(master).deriveSoftened(AbstractHD.PathType.EXTERNAL_ROOT_PATH.getValue()),60);

            EncryptedData encryptedData = new EncryptedData(seed, "abc", false);
            String encrypt = encryptedData.toEncryptedString();
            MyLog.i(TAG, "randomSeed :" + Utils.bytesToHexString(randomSeed));
            MyLog.i(TAG, "randomSeed Encrypt:" + encrypt);
            MyLog.i(TAG, "randomSeed decrypt:" + Utils.bytesToHexString(new EncryptedData(encrypt).decrypt("abc")));//mnemonic
            for (String str : words) {
                MyLog.i(TAG, "words :" + str);
            }
            MyLog.i(TAG, "mnemonicSeed :" + mnemonicSeed);
            MyLog.i(TAG, "512PrivateKey:" + priKey);
            MyLog.i(TAG, "512publicKey:" + pubkey);
            //MyLog.i(TAG,"firstAddr:"+firstAddr);


            for (String str : MnemonicCode.instance().toMnemonic(MnemonicCode.instance().toEntropy(words))) {
                MyLog.i(TAG, "words :" + str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.e(TAG, e + "");
        }

    }


    protected DeterministicKey getChainRootKey(DeterministicKey accountKey, AbstractHD.PathType
            pathType) {
        return accountKey.deriveSoftened(pathType.getValue());
    }


    /**
     * 创建随机数种子
     *
     * @return
     */
    public static byte[] createRandomSeed() {
        if (!isInstanced()) {
            MyLog.e("CreateAcountUtils", "doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        SecureRandom random = new SecureRandom();//创建随机类的实例
        byte[] randomSeed = new byte[16];//生成128位字节流

        //byte[] radomSeed = random.generateSeed(KeyCrypterScrypt.BLOCK_LENGTH);//生成128位字节流种子
        //MyLog.i(TAG,"radomSeed :"+ Utils.bytesToHexString(radomSeed));


        random.nextBytes(randomSeed);//生成128位随机数
        return randomSeed;
    }


    public static byte[] createMnemSeed(List<String> words) {
        if (!isInstanced()) {
            MyLog.e("CreateAcountUtils", "doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        return MnemonicCode.toSeed(words, "");//由助记词和密码生成种子,方法内含有转换512哈系数方式
        //return MnemonicCode.instance().toEntropy(words);//由助记词反推出随机数

    }

    public static List<String> getMnemonicCode(byte[] radomSeed) throws MnemonicException.MnemonicChecksumException, MnemonicException.MnemonicLengthException, MnemonicException.MnemonicWordException {
        if (!isInstanced()) {
            MyLog.e("CreateAcountUtils", "doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        //MnemonicCode.instance().toMnemonic()
        return MnemonicCode.instance().toMnemonic(radomSeed);//

    }

    public static byte[] entropyRandomSeed(List<String> words) throws MnemonicException.MnemonicChecksumException, MnemonicException.MnemonicLengthException, MnemonicException.MnemonicWordException {
        if (!isInstanced()) {
            MyLog.e("CreateAcountUtils", "doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }//
        return MnemonicCode.instance().toEntropy(words);
    }


    /**
     * 获取统一地址
     *
     * @param externalKey
     * @return
     */
    public static String getFirstAddress(DeterministicKey externalKey) {
        return getAddress(externalKey, 0);
    }

    /**
     * 获取指定的地址
     *
     * @param externalKey
     * @param i
     * @return
     */
    public static String getAddress(DeterministicKey externalKey, int i) {
        DeterministicKey key = externalKey.deriveSoftened(i);
        return key.toAddress();
    }

    /**
     * 生成根公私钥对象
     *
     * @param seed
     * @return
     */
    public static DeterministicKey CreateRootKey(byte[] seed) {
        if (!isInstanced()) {
            MyLog.e("CreateAcountUtils", "doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }

            /*HMac hMac = HDUtils.createHmacSha512Digest("mnemonic".getBytes());//生成512哈希数
            //byte[] hash512 = HDUtils.hmacSha512(hMac, seed);//生成512哈希数
            byte[] mnemhash512 = HDUtils.hmacSha512(hMac, mnemonicSeed);//生成512哈希数
            //MyLog.i(TAG,"seedhash512 :"+Utils.bytesToHexString(hash512));
            MyLog.i(TAG,"mnemonicSeedhash512 :"+Utils.bytesToHexString(mnemhash512));*/
        //List<byte[]> keys = getPrivateKey(hash512,"abcd");

        DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
        String mnemonicSeed = Utils.bytesToHexString(seed);//助记词生成的根种子
        String priKey = Utils.bytesToHexString(master.getPrivKeyBytes());//根私钥
        String pubkey = Utils.bytesToHexString(master.getPubKey());//根公钥
        return master;
    }


    /**
     * 获取内外部钥对象
     *
     * @param master
     * @return
     */
    public static DeterministicKey getAccount(DeterministicKey master, int coin_type) {
        if (!isInstanced()) {
            MyLog.e("CreateAcountUtils", "doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        DeterministicKey purpose = master.deriveHardened(44);
        DeterministicKey coinType = purpose.deriveHardened(coin_type);
        DeterministicKey account = coinType.deriveHardened(0);
        DeterministicKey account1 = account.deriveSoftened(0);
        DeterministicKey account2 = account1.deriveSoftened(0);
        purpose.wipe();
        coinType.wipe();
        account.wipe();
        account1.wipe();
        return account2;
    }


    /**
     * 获取内外部钥对象
     *
     * @param master
     * @return
     */
    public static String getWalletAddr(DeterministicKey master, int coin_type) {
        if (!isInstanced()) {
            MyLog.e("CreateAcountUtils", "doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        DeterministicKey purpose = master.deriveHardened(44);
        DeterministicKey coinType = purpose.deriveHardened(coin_type);
        DeterministicKey account = coinType.deriveHardened(0);
        DeterministicKey account1 = account.deriveSoftened(0);
        DeterministicKey account2 = account1.deriveSoftened(0);

        purpose.wipe();
        coinType.wipe();
        account.wipe();
        account1.wipe();
        return account2.toAddress1();
    }

    public static void saveCoins(DeterministicKey master, Context context,Callback callback) {
        try {
            try {
                SqliteUtils.execSQL("drop table coininfo");
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            SqliteUtils.execSQL("CREATE TABLE IF NOT EXISTS coininfo (id INTEGER PRIMARY KEY NOT NULL ,type INTEGER NOT NULL,name VARCHAR(42) NOT NULL,full_name VARCHAR(420) NOT NULL,addr_algorithm VARCHAR(42) NOT NULL,addr_algorithm_param TEXT,sign_algorithm VARCHAR(42) NOT NULL,sing_algorithm_param TEXT,token_type VARCHAR(42),token_addr VARCHAR(42),addr VARCHAR(42),decimals VARCHAR(42),amount VARCHAR(42),icon VARCHAR(42));");
            String json = CoinsUtil.getJson(context);
            List<CoinInfo> list = new Gson().fromJson(json, new TypeToken<List<CoinInfo>>() {
            }.getType());
            if (list != null) {
                for (CoinInfo coinInfo : list) {
                    MyLog.i(TAG,"btc coinInfo:"+coinInfo);
                    String addr = "";
                    if ("btc".equalsIgnoreCase(coinInfo.getAddr_algorithm())) {
                        addr = getAccount(master, coinInfo.getType()).toAddress();
                        MyLog.i(TAG,"btc addr:"+addr);
                    } else if ("eth".equalsIgnoreCase(coinInfo.getAddr_algorithm())) {
                        addr = getWalletAddr(master, coinInfo.getType());
                        MyLog.i(TAG,"eth addr:"+addr);
                    }
                    coinInfo.setAddr(addr);
                }
                DataSupport.saveAll(list);
            }
            callback.saveSuccess();
        } catch (Exception e) {
            callback.saveFailure();
            e.printStackTrace();
        }
    }

    public static void saveCoins(DeterministicKey master, RuntHTTPApi.ResPonse resPonse) {

        RuntHTTPApi.toReApi(RuntHTTPApi.URL_GET_COINS, new HashMap<>(), new RuntHTTPApi.MyStringCallBack(mContext, new RuntHTTPApi.ResPonse() {
            @Override
            public void doSuccessThing(final Map<String, Object> param) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            try {
                                SqliteUtils.execSQL("drop table coininfo");
                            } catch (SQLiteException e) {
                                e.printStackTrace();
                            }
                            SqliteUtils.execSQL("CREATE TABLE IF NOT EXISTS coininfo (id INTEGER PRIMARY KEY NOT NULL ,type INTEGER NOT NULL,name VARCHAR(42) NOT NULL,full_name VARCHAR(420) NOT NULL,addr_algorithm VARCHAR(42) NOT NULL,addr_algorithm_param TEXT,sign_algorithm VARCHAR(42) NOT NULL,sing_algorithm_param TEXT,token_type VARCHAR(42),token_addr VARCHAR(42),addr VARCHAR(42),decimals VARCHAR(42),amount VARCHAR(42),icon VARCHAR(42));");
                            //,decimals INTEGER,is_default BOOLEAN
                            SqliteUtils.test();
                            MyLog.i("StartActivity", (param.get("data") instanceof Collection) + "");
                            if (param.get("data") instanceof Collection) {
                                for (Map map : (List<Map>) param.get("data")) {
                                    CoinInfo coinInfo = new CoinInfo(map);
                                    MyLog.i(TAG, coinInfo.getName() + "信息正在保存");
                                    if (coinInfo.getName().toLowerCase().equals("btc")) {
                                        String addr = getAccount(master, coinInfo.getType()).toAddress();
                                        MyLog.i(TAG, "addr:" + addr);
                                        coinInfo.setAddr(addr);
                                    } else if (coinInfo.getName().toLowerCase().equals("eth")) {
                                        String addr = CreateAcountUtils.getWalletAddr(master, coinInfo.getType());
                                        MyLog.i(TAG, "addr:" + addr);
                                        coinInfo.setAddr(addr);
                                        param.put("firstAddr", addr);
                                    }
                                    coinInfo.save();
                                    MyLog.i(TAG, "信息保存成功");

                                }

                            }
                            resPonse.doSuccessThing(param);
                        } catch (Exception e) {
                            e.printStackTrace();
                            param.put("error", e.getMessage());
                            resPonse.doErrorThing(param);
                        }
                    }
                }.start();
            }

            @Override
            public void doErrorThing(Map<String, Object> param) {
                resPonse.doErrorThing(param);
            }
        }));

    }

    public static void saveWallet(String randomSeed, String mnemonicSeed, String firstAddr, RuntHTTPApi.ResPonse resPonse) {

        try {
            Map<String, Object> param = new HashMap<>();
            List<WalletInfo> list = new ArrayList<>();
            try {

                List<Map> maps = SqliteUtils.selecte("walletinfo");
                for (Map map : maps) {
                    list.add(new WalletInfo(map));
                }
                if (list.size() > 0 && !list.get(0).getAddress().equals(firstAddr)) {//判断生成的同一地址，是否与数据库相同
                    if (randomSeed != null && !randomSeed.equals("") && !randomSeed.equals("null")) {
                        param.put("error", "助记词有误，请重新输入");
                    } else {
                        param.put("error", "私钥有误，请重新输入");
                    }
                    resPonse.doErrorThing(param);
                    return;
                }
                if (list.size() > 0) {
                    PreferencesUtils.putString(mContext, BaseActivity.KEY_WALLET_NAME, list.get(0).getName(), PreferencesUtils.VISITOR);
                    PreferencesUtils.putString(mContext, BaseActivity.KEY_WALLET_PWD_TIP, list.get(0).getTip(), PreferencesUtils.VISITOR);
                    param.put("success", "密码修改成功");
                }
                SqliteUtils.execSQL("drop table walletinfo");
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            WalletInfo walletInfo = new WalletInfo(mContext);
            walletInfo.setName(PreferencesUtils.getString(mContext, BaseActivity.KEY_WALLET_NAME, PreferencesUtils.VISITOR));
            walletInfo.setTip(PreferencesUtils.getString(mContext, BaseActivity.KEY_WALLET_PWD_TIP, PreferencesUtils.VISITOR));
            walletInfo.setEsda_seed(new EncryptedData(Utils.hexStringToByteArray(mnemonicSeed),
                    PreferencesUtils.getString(mContext, BaseActivity.KEY_WALLET_PWD, PreferencesUtils.VISITOR),
                    false)
                    .toEncryptedString());
            if (randomSeed != null && !randomSeed.equals("") && randomSeed.indexOf("null") == -1) {
                walletInfo.setMnem_seed(new EncryptedData(Utils.hexStringToByteArray(randomSeed),
                        PreferencesUtils.getString(mContext, BaseActivity.KEY_WALLET_PWD, PreferencesUtils.VISITOR),
                        false)
                        .toEncryptedString());
            }
            walletInfo.setAddress(firstAddr);

            ContentValues cValue = new ContentValues();
            for (Object key : walletInfo.toMap().keySet()) {
                if (key.toString().indexOf("id") == -1) {
                    cValue.put(key.toString(), walletInfo.toMap().get(key) + "");
                }
            }
            SqliteUtils.insert("walletinfo", cValue);
            MyLog.i(TAG, "钱包数据保存成功");
            resPonse.doSuccessThing(param);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> param = new HashMap<>();
            param.put("error", e.getMessage());
            resPonse.doErrorThing(param);
        }
    }


    /**
     * 读取词库
     */
    private static final class MnemonicCodeTestClass extends MnemonicCode {
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
     *
     * @param seed
     * @param pwd
     * @return
     */
    public List<byte[]> getPrivateKey(byte[] seed, String pwd) {
        if (!isInstanced()) {
            MyLog.e("CreateAcountUtils", "doesn't new this class, please use the method 'instance()' first");
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
        return list;
    }


    public static HDAccount createADAccount(byte[] mnemseed, String pwd) throws MnemonicException.MnemonicLengthException {
        if (!isInstanced()) {
            MyLog.e("CreateAcountUtils", "doesn't new this class, please use the method 'instance()' first");
            throw new NullPointerException();
        }
        return new HDAccount(mnemseed, pwd);
    }


    private void nothing(String pwd) {


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
                                    MyLog.i(TAG,"progress :"+progress);
                                }
                            });*//*
        hdAccount.getPubKey();
        MyLog.i(TAG,"hdAccount :"+ Base58.decode(pwd));
                            *//*AbstractDb.construct();
                            List<Integer> seeds = AbstractDb.hdAccountProvider.getHDAccountSeeds();
                            for (int seedId : seeds) {
                                MyLog.i(TAG,"seedId :"+seedId);
                            }
                            new WalletInfo(new EncryptedData(encreyptString)
                                    , password, false)*/
    }


    private static boolean isInstanced() {
        if (mContext == null) {
            return false;
        }
        return true;
    }

    public interface Callback{
        void saveSuccess();
        void saveFailure();
    }

}
