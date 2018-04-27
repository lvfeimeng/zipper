package com.zipper.wallet.ether;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.zipper.wallet.utils.ether.Bytes;
import com.zipper.wallet.utils.ether.RlpEncoder;
import com.zipper.wallet.utils.ether.RlpList;
import com.zipper.wallet.utils.ether.RlpString;
import com.zipper.wallet.utils.ether.RlpType;

import net.bither.bitherj.crypto.ECKey;

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

    private SignEther.SignatureData signatureData;

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
        return new EtherRawTransaction(nonce, gasPrice, gasLimit, to, value, data, (byte)0);
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

    public SignEther.SignatureData getSignatureData() {
        return signatureData;
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
        return result;
    }
    
    public byte[] getEncodedRaw() {
        List<RlpType> result = this.getEncodedBasic();
        if (chainId != 0) {
            // Since EIP-155 use chainId for v
            SignEther.SignatureData tsignatureData = new SignEther.SignatureData(chainId, new byte[] {}, new byte[] {});
            result.add(RlpString.create(tsignatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getS())));
        }
        RlpList rlpList = new RlpList(result);
        return RlpEncoder.encode(rlpList);
    }

    public byte[] getEncoded() {
        List<RlpType> result = this.getEncodedBasic();
        if (signatureData != null) {
            int encodeV = signatureData.getV() +  LOWER_REAL_V;
            if (chainId != 0) {
                encodeV += chainId * 2 + CHAIN_ID_INC  - LOWER_REAL_V;;
            }
            SignEther.SignatureData tsignatureData = new SignEther.SignatureData(
                    (byte)encodeV, signatureData.getR(), signatureData.getS());
            result.add(RlpString.create(tsignatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getS())));
        } else if (chainId != 0){
            // Since EIP-155 use chainId for v
            SignEther.SignatureData tsignatureData = new SignEther.SignatureData(chainId, new byte[] {}, new byte[] {});
            result.add(RlpString.create(tsignatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(tsignatureData.getS())));
        }
        RlpList rlpList = new RlpList(result);
        return RlpEncoder.encode(rlpList);
    }

    public byte[] Sign(ECKey  key) {
        this.signatureData = SignEther.signMessage(this.getEncodedRaw(),key);
        return  this.getEncoded();
    }


}
