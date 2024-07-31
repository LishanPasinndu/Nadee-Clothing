package lk.jiat.eshop.model;

import java.util.List;

public class Product {

    private String pid;
    private String image;
    private String name;
    private String category;
    private String desc;
    private List<String> sizes;
    private String weight;
    private Double price;
    private Integer qty;

    public Product() {
    }

    public Product(String pid, String image, String name, String category, String desc, List<String> sizes, String weight, Double price, Integer qty) {
        this.pid = pid;
        this.image = image;
        this.name = name;
        this.category = category;
        this.desc = desc;
        this.sizes = sizes;
        this.weight = weight;
        this.price = price;
        this.qty = qty;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
