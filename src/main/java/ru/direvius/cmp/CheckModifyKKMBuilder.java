package ru.direvius.cmp;

/**
 *
 * @author direvius
 */
public class CheckModifyKKMBuilder implements RequestBuilder {

    //private final TLVChainBuilder tcb = new TLVChainBuilder();
    private static final byte [] respFuel = Util.hexStringToByteArray("01-01-06-03-04-00-02-0D-00-04-02-00-04-08-44-01-1C-01-02-47-32-02-04-00-00-2E-E0-03-04-00-00-2B-C0-04-04-00-02-0D-00-05-04-80-2D-39-32-03-04-00-02-0D-00-0A-1E-01-04-00-02-0D-00-02-04-00-00-00-00-03-04-00-00-00-00-04-04-00-00-00-00-05-04-00-00-00-00");
    private static final byte [] respSTiU = Util.hexStringToByteArray("01-01-06-03-04-00-03-D0-90-04-02-00-04-08-81-96-01-47-01-07-58-37-31-34-36-35-32-02-04-00-00-03-E8-03-04-00-00-C3-50-04-04-00-00-C3-50-05-2A-8E-E7-A5-AD-EC-20-A4-AB-A8-AD-AD-AE-A5-20-AD-A0-A7-A2-A0-AD-A8-A5-20-E2-AE-A2-A0-E0-A0-20-A4-AB-EF-20-AF-E0-AE-A2-A5-E0-AA-A8-01-25-01-07-58-37-31-34-36-35-31-02-04-00-00-03-E8-03-04-00-03-0D-40-04-04-00-03-0D-40-05-08-AC-AE-A9-AA-A0-8D-85-92-03-04-00-03-D0-90-0A-1E-01-04-00-03-D0-90-02-04-00-00-00-00-03-04-00-00-00-00-04-04-00-00-00-00-05-04-00-00-00-00");
    public enum CheckType{Fuel, STiU}
    private CheckType checkType;
    public CheckModifyKKMBuilder(CheckType checkType){
        this.checkType = checkType;
    }
    public byte[] build() {
        switch(checkType){
            case Fuel:
                return respFuel;
            case STiU:
                return respSTiU;
            default:
                throw new IllegalStateException("Unknown check type");
        }
    }
}
