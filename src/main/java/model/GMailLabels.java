package model;

public enum GMailLabels {

    INBOX("INBOX"),
    SENT("SENT"),
    SUPPLIER("Поставщики"),
    DELIVERY("Доставка"),
    BERIVDOROGU("Бери в дорогу"),
    IDIVPERED("Иди вперед");

    String name;

    GMailLabels(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
