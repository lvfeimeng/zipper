package com.zipper.wallet.ether;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import com.zipper.wallet.ether.abi.EventEncoder;
import com.zipper.wallet.ether.abi.FunctionEncoder;
import com.zipper.wallet.ether.abi.TypeReference;
import com.zipper.wallet.ether.abi.datatypes.Address;
import com.zipper.wallet.ether.abi.datatypes.Bool;
import com.zipper.wallet.ether.abi.datatypes.Event;
import com.zipper.wallet.ether.abi.datatypes.Utf8String;
import com.zipper.wallet.ether.abi.datatypes.generated.Uint8;
import com.zipper.wallet.ether.abi.datatypes.Function;
import com.zipper.wallet.ether.abi.datatypes.generated.Uint256;

/**
 * Created by chaogaofeng on 2018/5/5.
 */

public class ERC20Token {
    private String deploy(BigInteger initialSupply, String tokenName, BigInteger decimalUnits, String tokenSymbol) throws Exception {

        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.asList(
                                new Uint256(initialSupply),
                                new Utf8String(tokenName),
                                new Uint8(decimalUnits),
                                new Utf8String(tokenSymbol)));


        String cbin = "";
        return cbin + encodedConstructor;
    }

    private String totalSupply() {
        return FunctionEncoder.encode(new Function(
                "totalSupply",
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {})));
    }

    private String balanceOf(String owner) {
        return FunctionEncoder.encode(new Function(
                "balanceOf",
                Collections.singletonList(new Address(owner)),
                Collections.singletonList(new TypeReference<Uint256>() {})));
    }

    public static String transfer(String to, BigInteger value) {
        return FunctionEncoder.encode(new Function(
                "transfer",
                Arrays.asList(new Address(to), new Uint256(value)),
                Collections.singletonList(new TypeReference<Bool>() {})));
    }

    private String allowance(String owner, String spender) {
        return FunctionEncoder.encode(new Function(
                "allowance",
                Arrays.asList(new Address(owner), new Address(spender)),
                Collections.singletonList(new TypeReference<Uint256>() {})));
    }

    private String approve(String spender, BigInteger value) {
        return FunctionEncoder.encode(new Function(
                "approve",
                Arrays.asList(new Address(spender), new Uint256(value)),
                Collections.singletonList(new TypeReference<Bool>() {})));
    }

    private String transferFrom(String from, String to, BigInteger value) {
        return FunctionEncoder.encode(new Function(
                "transferFrom",
                Arrays.asList(new Address(from), new Address(to), new Uint256(value)),
                Collections.singletonList(new TypeReference<Bool>() {})));
    }

    private String transferEvent() {
        return EventEncoder.encode(new Event(
                "Transfer",
                Arrays.asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Collections.singletonList(new TypeReference<Uint256>() {})));
    }

    private String approvalEvent() {
        return EventEncoder.encode(new Event(
                "Approval",
                Arrays.asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Collections.singletonList(new TypeReference<Uint256>() {})));
    }
}