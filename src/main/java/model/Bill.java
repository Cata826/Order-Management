package model;


public record Bill(int orderId, int clientId, int productId, int value) {

    public Bill {
        if (orderId < 0) {
            throw new IllegalArgumentException("Invalid orderId: " + orderId);
        }
        if (clientId < 0) {
            throw new IllegalArgumentException("Invalid clientId: " + clientId);
        }
        if (productId < 0) {
            throw new IllegalArgumentException("Invalid productId: " + productId);
        }
        if (value < 0) {
            throw new IllegalArgumentException("Invalid value: " + value);
        }
    }

    public int orderId() {
        return orderId;
    }

    public int clientId() {
        return clientId;
    }

    public int productId() {
        return productId;
    }

    public int value() {
        return value;
    }

}
