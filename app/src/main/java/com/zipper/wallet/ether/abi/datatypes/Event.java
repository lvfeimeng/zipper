package com.zipper.wallet.ether.abi.datatypes;

import java.util.List;

import com.zipper.wallet.ether.abi.TypeReference;
import com.zipper.wallet.ether.abi.datatypes.Type;

import static com.zipper.wallet.ether.abi.Utils.convert;

/**
 * Event wrapper type.
 */
public class Event {
    private String name;
    private List<TypeReference<Type>> indexedParameters;
    private List<TypeReference<Type>> nonIndexedParameters;

    public Event(String name, List<TypeReference<?>> indexedParameters,
                 List<TypeReference<?>> nonIndexedParameters) {
        this.name = name;
        this.indexedParameters = convert(indexedParameters);
        this.nonIndexedParameters = convert(nonIndexedParameters);
    }

    public String getName() {
        return name;
    }

    public List<TypeReference<Type>> getIndexedParameters() {
        return indexedParameters;
    }

    public List<TypeReference<Type>> getNonIndexedParameters() {
        return nonIndexedParameters;
    }
}
