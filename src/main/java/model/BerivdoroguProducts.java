package model;

import java.math.BigDecimal;

public class BerivdoroguProducts extends PriceImpl implements Price {

    private String model;
    private boolean status;
    private BigDecimal oldRetailPrice;
    private boolean presentInPrice = false;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public BigDecimal getOldRetailPrice() {
        return oldRetailPrice;
    }

    public void setOldRetailPrice(BigDecimal oldRetailPrice) {
        this.oldRetailPrice = oldRetailPrice;
    }

    public boolean isPresentInPrice() {
        return presentInPrice;
    }

    public void setPresentInPrice(boolean presentInPrice) {
        this.presentInPrice = presentInPrice;
    }
}
