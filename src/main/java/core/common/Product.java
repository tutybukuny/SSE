package core.common;

public class Product {
    private int index;
    private String productName;
    private double grade;

    public Product(int index, String productName) {
        this.index = index;
        this.productName = productName;
        grade = 0;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}
