package com.zipper.wallet.impclasses;

import net.bither.bitherj.db.IHDAccountProvider;

import java.util.List;

/**
 * Created by Administrator on 2018/4/10.
 */

public class MyIHDAccountProvider implements IHDAccountProvider {



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
}
