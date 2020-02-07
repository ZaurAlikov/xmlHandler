package ru.alcotester.pricehandler.model;

public enum VendorEnum {

    ESAUTO ("01", "es-auto"),
    EURODETAL ("02", "eurodetal"),
    BAGAJNIK ("03", "bagajnik"),
    ATUNING ("04", "a-tuning");

    private String code;

    private String name;

    VendorEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
