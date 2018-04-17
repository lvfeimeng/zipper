package com.zipper.wallet;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.WordList;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

public class Test {

    class Album extends DataSupport {

        @Column(unique = true, defaultValue = "unknown")
        private String name;

        private float price;

        private byte[] cover;

    }

    private void t() {
        Album album = new Album();
        album=DataSupport.find(Album.class,1);

    }

    public static void main(String[] args) {


        //test1();
        test2();
        test3();
    }

    private static void test3() {
//        byte[] seed = new SeedCalculator()
//                .withWordsFromWordList(English.INSTANCE)
//                .calculateSeed(mnemonicWord;sInAList, passphrase);
    }

    private static void test2() {
        String mnemonic = "sort spoon motion country tornado vacuum hat magic rotate outer spy unfold";
        String passphrase = "1234567890";
        byte[] seed = new SeedCalculator().calculateSeed(mnemonic, passphrase);
        //1111111
        //000000000000000000000000000000
    }

    private static void test1() {
        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE)
                .createMnemonic(entropy, sb::append);
        System.out.println("--->" + sb.toString());
    }


}
