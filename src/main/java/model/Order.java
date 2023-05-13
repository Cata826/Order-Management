package model;

public class Order {
    private int orderId;
    private int client;
    private int product;
    private int quantity;

    public Order(int orderId, int client, int product, int quantity) {
        this.orderId = orderId;
        this.client = client;
        this.product = product;
        this.quantity = quantity;
    }
    public Order() {
    }
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public int getClient() {
        return client;
    }

    public void setClient(int client) {
        this.client = client;
    }
    public int getProduct() {
        return product;
    }
    public void setProduct(int product) {
        this.product = product;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
