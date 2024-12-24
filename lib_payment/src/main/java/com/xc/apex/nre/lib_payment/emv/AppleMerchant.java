package com.xc.apex.nre.lib_payment.emv;

public class AppleMerchant {

    private byte[] merchantId;
    private byte[] merchantUrl;
    private byte[] vasFilter;

    public AppleMerchant() {
    }

    public AppleMerchant(byte[] merchantId, byte[] merchantUrl, byte[] vasFilter) {
        this.merchantId = merchantId;
        this.merchantUrl = merchantUrl;
        this.vasFilter = vasFilter;
    }

    public byte[] getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(byte[] merchantId) {
        this.merchantId = merchantId;
    }

    public byte[] getMerchantUrl() {
        return merchantUrl;
    }

    public void setMerchantUrl(byte[] merchantUrl) {
        this.merchantUrl = merchantUrl;
    }

    public byte[] getVasFilter() {
        return vasFilter;
    }

    public void setVasFilter(byte[] vasFilter) {
        this.vasFilter = vasFilter;
    }
}
