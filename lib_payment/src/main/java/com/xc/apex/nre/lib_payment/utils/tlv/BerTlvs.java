package com.xc.apex.nre.lib_payment.utils.tlv;

import java.util.ArrayList;
import java.util.List;

public class BerTlvs {

    private final List<BerTlv> tlvs;

    protected BerTlvs(List<BerTlv> aTlvs) {
        tlvs = aTlvs;
    }

    public BerTlv find(BerTag aTag) {
        for (BerTlv tlv : tlvs) {
            BerTlv found = tlv.find(aTag);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public List<BerTlv> findAll(BerTag aTag) {
        List<BerTlv> list = new ArrayList<>();
        for (BerTlv tlv : tlvs) {
            list.addAll(tlv.findAll(aTag));
        }
        return list;
    }

    public List<BerTlv> getList() {
        return tlvs;
    }
}
