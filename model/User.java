package lk.jiat.eshop.model;

public class User {

    private String username, email, mobile, password, addressLine1, addressLine2, city, DeliveryMobile;

    public User() {
    }

    public User(String username, String email, String mobile, String password, String addressLine1, String addressLine2, String city, String deliveryMobile) {
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.DeliveryMobile = deliveryMobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDeliveryMobile() {
        return DeliveryMobile;
    }

    public void setDeliveryMobile(String deliveryMobile) {
        DeliveryMobile = deliveryMobile;
    }
}