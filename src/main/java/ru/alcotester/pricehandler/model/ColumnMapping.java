package ru.alcotester.pricehandler.model;

public class ColumnMapping {

    //Продавец
    private VendorEnum vendor;

    //Наименование товара
    private Integer productName;
    //Артикул товара
    private Integer sku;
    //Розничная цена
    private Integer retailPrice;
    //Оптовая цена
    private Integer tradePrice;
    //Единица измерения
    private Integer unit;
    //Наличие
    private Integer availability;

    public VendorEnum getVendor() {
        return vendor;
    }

    public void setVendor(VendorEnum vendor) {
        this.vendor = vendor;
    }

    public Integer getProductName() {
        return productName;
    }

    public void setProductName(Integer productName) {
        this.productName = productName;
    }

    public Integer getSku() {
        return sku;
    }

    public void setSku(Integer sku) {
        this.sku = sku;
    }

    public Integer getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(Integer retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Integer getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Integer tradePrice) {
        this.tradePrice = tradePrice;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }
}
