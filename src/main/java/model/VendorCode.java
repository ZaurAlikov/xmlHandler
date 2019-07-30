package model;

public enum VendorCode {

    ESAUTO ("01"),
    EURODETAL ("02"),
    BAGAJNIK ("03"),
    ATUNING ("04");

    private String code;

    VendorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
