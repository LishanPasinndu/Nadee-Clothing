package lk.jiat.eshop.model;

public class Orders {

    private String user;
    private String pid;
    private String sizes;
    private String qty;
    private Double Total;
    private String pname;
    private String imagepath;
    private Double price;
    private String Date;
    private String Time;
    private String Status;
    private String orderId;

    public Orders() {
    }

    public Orders(String user, String pid, String sizes, String qty, Double total, String date, String time,String status,String Id) {
        this.user = user;
        this.pid = pid;
        this.sizes = sizes;
        this.qty = qty;
        this.Date = date;
        this.Time = time;
        this.Status = status;
        this.orderId = Id;
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }
    public void setTime(String time) {
        Time = time;
    }
    public String getStatus() {
        return Status;
    }
    public void setStatus(String status) {
        Status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
