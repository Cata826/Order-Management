package model;

/**
 *
 */
public class Client {
    private int clientId;
    private String name;
    private String contact;
    private String address;

    public Client(int clientId, String name, String address,String contact) {
        this.clientId = clientId;
        this.name = name;
        this.contact = contact;
        this.address = address;
    }
    public Client() {
    }
    public int getClientId() {
        return clientId;
    }
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

}
