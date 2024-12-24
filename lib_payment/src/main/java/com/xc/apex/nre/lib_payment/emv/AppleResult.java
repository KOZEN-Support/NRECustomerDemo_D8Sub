package com.xc.apex.nre.lib_payment.emv;

public enum AppleResult {

    Success(0, "Successful"),
    Communication(1, "The NFC communication channel with the mobile has been broken"),
    WrongCommand(2, "The mobile says that our command is erroneous"),
    WrongResponseStatusWord(3, "The mobile's has reported an error in its status word"),
    WrongResponseData(4, "The mobile's response is invalid"),
    DataMissing(5, "The mobile does not have any VAS data, or its configuration is broken"),
    NoVasApplication(6, "The mobile does not run the VAS application"),
    VasVersionConflict(7, "The mobile runs an unsupported version of the VAS application"),
    VasDisabled(8, "The mobile says we should skip the VAS protocol"),
    NoVasMerchant(9, "The mobile has no available pass for this merchant"),
    NoVasMessage(10, "The mobile's response does not contain a VAS message for our merchant"),
    WaitingForIntervention(11, "The mobile is waiting for user intervention"),
    WaitingForActivation(12, "The mobile is waiting for user activation"),
    UnknownMerchantPublicKeyId(13, "The mobile announced a merchant public key id that the terminal does not know"),
    NoMobilePublicKey(14, "The mobile did not provide its public key"),
    KeyDerivationFailed(15, "The terminal has failed to compute the shared key"),
    MessageDecipherFailed(16, "A message has been received for the given merchant, but a decryption error has occurred"),
    VasToPayment(30, "Go to Payment");

    public final int    errCode;
    public final String name;

    AppleResult(final int errCode, final String name) {
        this.errCode = errCode;
        this.name = name;
    }

    public static AppleResult getVasErrorByCode(final int code) {
        AppleResult ret = null;
        for (AppleResult val : AppleResult.values()) {
            if (code == val.errCode) {
                ret = val;
                break;
            }
        }
        return ret;
    }
}
