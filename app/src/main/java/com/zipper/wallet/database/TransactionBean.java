package com.zipper.wallet.database;

import org.litepal.crud.DataSupport;

/**
 * Created by admin on 2018/4/24.
 */

public class TransactionBean extends DataSupport {

    private int timestamp;
    private int block_number;
    private String id;
    private String from;
    private String to;
    private int value;
    private String remark;
    private int spice;
    private int miner_sprice;


}
