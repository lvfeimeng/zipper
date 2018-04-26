package com.zipper.wallet.transfer;

import com.zipper.wallet.transfer.rlp.RlpEncoder;
import com.zipper.wallet.transfer.rlp.RlpList;
import com.zipper.wallet.transfer.rlp.RlpString;
import com.zipper.wallet.transfer.rlp.RlpType;
import com.zipper.wallet.transfer.utils.Bytes;
import com.zipper.wallet.transfer.utils.KECCAK256;
import com.zipper.wallet.transfer.utils.Numeric;

import net.bither.bitherj.crypto.ECKey;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

class ChainId {
    public static final byte ANY = 0;
    public static final byte HOMESTEAD = 1;
    public static final byte MORDEN = 2;
    public static final byte ROPSTEN = 3;
    public static final byte RINKEBY = 4;
    public static final byte ROOTSTOCK_MAINNET = 30;
    public static final byte ROOTSTOCK_TESTNET = 31;
    public static final byte KOVAN = 42;
    public static final byte ETHEREUM_CLASSIC_MAINNET = 61;
    public static final byte ETHEREUM_CLASSIC_TESTNET = 62;
}

/**
 * Transaction class used for signing transactions locally.
 */
public class EtherRawTransaction {
    public static final BigInteger DEFAULT_GAS_PRICE = new BigInteger("10000000000000");
    public static final BigInteger DEFAULT_GAS_LIMIT = new BigInteger("21000");
    public static final int HASH_LENGTH = 32;
    public static final int ADDRESS_LENGTH = 20;

    private static final int CHAIN_ID_INC = 35;
    private static final int LOWER_REAL_V = 27;

    private BigInteger nonce;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private String to;
    private BigInteger value;
    private String data;
    private byte chainId;

    private ECKey.ECDSASignature signature;

    protected EtherRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
                           BigInteger value, String data, byte chainId) {
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.to = to;
        this.value = value;

        if (data != null) {
            this.data = Numeric.cleanHexPrefix(data);
        }
        this.chainId = chainId;
    }

    public static EtherRawTransaction createTransaction(
            BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data) {
        return new EtherRawTransaction(nonce, gasPrice, gasLimit, to, value, data, (byte) 0);
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getValue() {
        return value;
    }

    public String getData() {
        return data;
    }

    public ECKey.ECDSASignature getSignatureData() {
        return signature;
    }

    public byte[] sign(ECKey key) {
        this.signature = key.sign(KECCAK256.keccak256(this.getEncodedRaw()));
        return getEncoded();
    }

    public byte[] signHash(ECKey key) {
        this.signature = key.sign(KECCAK256.keccak256(this.getEncodedRaw()));
        return KECCAK256.keccak256(this.getEncoded());
    }

    private List<RlpType> getEncodedBasic() {
        List<RlpType> result = new ArrayList<>();
        result.add(RlpString.create(this.getNonce()));
        result.add(RlpString.create(this.getGasPrice()));
        result.add(RlpString.create(this.getGasLimit()));
        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = this.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }
        result.add(RlpString.create(this.getValue()));
        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(this.getData());
        result.add(RlpString.create(data));
        return result
    }
    
    public byte[] getEncodedRaw() {
        List<RlpType> result = this.getEncodedBasic();
        if (chainId != 0) {
            // Since EIP-155 use chainId for v
            ECKey.ECDSASignature tsignatureData = new ECKey.ECDSASignature(chainId, new byte[] {}, new byte[] {});
            result.add(RlpString.create(tsignatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getS())));
        }
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    public byte[] getEncoded() {
        List<RlpType> result = this.getEncodedBasic();
        if (signatureData != null) {
            int encodeV;
            if (chainId == 0) {
                encodeV = signature.v;
            } else {
                encodeV = signature.v - LOWER_REAL_V;
                encodeV += chainId * 2 + CHAIN_ID_INC;
            }
            ECDSASignature tsignatureData = new Sign.SignatureData(
                v, signatureData.getR(), signatureData.getS());
            result.add(RlpString.create(tsignatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getS())));
        } else {
            // Since EIP-155 use chainId for v
            ECDSASignature tsignatureData = new ECDSASignature(chainId, new byte[] {}, new byte[] {});
            result.add(RlpString.create(tsignatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getS())));
        }
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }
}
