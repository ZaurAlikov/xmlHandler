package model;

import java.math.BigDecimal;

public class BerivdoroguProducts extends PriceImpl implements Price {

    private String bdCategory;
    private String model;
    private String manufacturer;
    private int quantity;
    private String metaTitle;
    private String metaDescription;
    private String description;
    private String image;
    private int sortOrder;
    private boolean status;
    private String seoKeyword;
    private String atributes;
    private String images;

    private BigDecimal oldRetailPrice;
    private Boolean oldStatus;
    private int oldQuantity;
    private boolean presentInPrice = false;

    public String getBdCategory() {
        return bdCategory;
    }

    public void setBdCategory(String bdCategory) {
        this.bdCategory = bdCategory;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getSeoKeyword() {
        return seoKeyword;
    }

    public void setSeoKeyword(String seoKeyword) {
        this.seoKeyword = seoKeyword;
    }

    public String getAtributes() {
        return atributes;
    }

    public void setAtributes(String atributes) {
        this.atributes = atributes;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public BigDecimal getOldRetailPrice() {
        return oldRetailPrice;
    }

    public void setOldRetailPrice(BigDecimal oldRetailPrice) {
        this.oldRetailPrice = oldRetailPrice;
    }

    public Boolean isOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(Boolean oldStatus) {
        this.oldStatus = oldStatus;
    }

    public int getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(int oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public boolean isPresentInPrice() {
        return presentInPrice;
    }

    public void setPresentInPrice(boolean presentInPrice) {
        this.presentInPrice = presentInPrice;
    }
}
