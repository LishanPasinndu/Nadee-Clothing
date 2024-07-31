package lk.jiat.eshop.model;

import java.util.List;

public class Cart {

    private String user;
    private String pid;
    private String sizes;
    private String qty;
    private Double Total;
    private String pname;
    private String imagepath;
    private Double price;

    public Cart() {
    }

    public Cart(String user, String pid, String sizes, String qty, Double total) {
        this.user = user;
        this.pid = pid;
        this.sizes = sizes;
        this.qty = qty;
        Total = total;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Double getTotal() {
        return Total;
    }

    public void setTotal(Double total) {
        Total = total;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
