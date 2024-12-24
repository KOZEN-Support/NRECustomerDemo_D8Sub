package com.xc.apex.nre.lib_payment.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

@Entity(tableName = "trans")
public final class TransactionData implements Serializable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String   transId;
    private int      cardType;
    private int      transType;
    private int      transResult;
    private int      appleVasResult;
    private Double   transAmount;
    private Double   transAmountOther;
    private Calendar transDate;
    private byte[]   transData;
    private byte[]   appleVasData;

    public TransactionData() {
        this.transDate = Calendar.getInstance();
    }

    @NonNull
    public String getTransId() {
        return transId;
    }

    public void setTransId(@NonNull String transId) {
        this.transId = transId;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public Double getTransAmountOther() {
        return transAmountOther;
    }

    public void setTransAmountOther(Double transAmountOther) {
        this.transAmountOther = transAmountOther;
    }

    public int getTransResult() {
        return transResult;
    }

    public void setTransResult(int transResult) {
        this.transResult = transResult;
    }

    public int getAppleVasResult() {
        return appleVasResult;
    }

    public void setAppleVasResult(int appleVasResult) {
        this.appleVasResult = appleVasResult;
    }

    public Double getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(Double transAmount) {
        this.transAmount = transAmount;
    }

    public Calendar getTransDate() {
        return transDate;
    }

    public void setTransDate(Calendar transDate) {
        this.transDate = transDate;
    }

    public byte[] getTransData() {
        return transData;
    }

    public void setTransData(byte[] transData) {
        this.transData = transData;
    }

    public byte[] getAppleVasData() {
        return appleVasData;
    }

    public void setAppleVasData(byte[] appleVasData) {
        this.appleVasData = appleVasData;
    }

    @NonNull
    @Override
    public String toString() {
        return transId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof TransactionData
                && this.transId.equals(((TransactionData) obj).transId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transId);
    }

    @Override
    protected Object clone() {
        return new TransactionData();
    }
}
