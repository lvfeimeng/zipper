package com.zipper.wallet.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.activity.BackUpAcitivty;

import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.crypto.hd.HDKeyDerivation;
import net.bither.bitherj.crypto.mnemonic.MnemonicCode;
import net.bither.bitherj.crypto.mnemonic.MnemonicException;
import net.bither.bitherj.utils.Sha256Hash;
import net.bither.bitherj.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * 助记词.
 */
public class MnemonicWordFragment extends Fragment {


    protected View rootView;
    protected EditText editWord;
    protected EditText editPassword;
    protected EditText editConfirmPassword;
    protected EditText editPasswordHint;
    protected CheckBox checkBox;
    protected TextView textAgreement;
    protected Button btnImport;
    protected TextView textWord;

    public MnemonicWordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_mnemonic_word, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        editWord = (EditText) rootView.findViewById(R.id.editWord);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) rootView.findViewById(R.id.editConfirmPassword);
        editPasswordHint = (EditText) rootView.findViewById(R.id.editPasswordHint);
        checkBox = (CheckBox) rootView.findViewById(R.id.checkBox);
        textAgreement = (TextView) rootView.findViewById(R.id.textAgreement);
        btnImport = (Button) rootView.findViewById(R.id.btnImport);
        textWord = (TextView) rootView.findViewById(R.id.textWord);
        btnImport.setOnClickListener(v -> importWallet());
    }

    private void importWallet() {
        try{
            MnemonicCode.setInstance(new MnemonicCodeTestClass());
            SecureRandom random=new SecureRandom();
            byte[] mnemonicSeed=new byte[16];
            random.nextBytes(mnemonicSeed);
            List<String> words=getMnemonicCode(Utils.hexStringToByteArray(Utils.bytesToHexString(mnemonicSeed)));
            byte[] seed = MnemonicCode.toSeed(words,"");
            //Utils.bytesToHexString(seed)
            DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);

             Utils.bytesToHexString(master.getPrivKeyBytes());
             Utils.bytesToHexString(master.getPubKey());
        }catch(Exception e){
            e.printStackTrace();
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
    public static ArrayList<String> wordList = new ArrayList<String>(2048);
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
            InputStream stream = getActivity().getAssets().open(WordListPath);
            if (stream == null) {
                throw new FileNotFoundException(WordListPath);
            }
            return stream;
        }
    }


}
