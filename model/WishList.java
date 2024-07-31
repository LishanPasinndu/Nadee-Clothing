package lk.jiat.eshop.model;

public class WishList {

    private String user;
    private String pid;
    private String sizes;
    private String pname;
    private String imagepath;
    private Double price;
    private String Date;
    private String Time;

    public WishList() {
    }

    public WishList(String user, String pid, String sizes, String date, String time) {
        this.user = user;
        this.pid = pid;
        this.sizes = sizes;
        this.Date = date;
        this.Time = time;
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
}
