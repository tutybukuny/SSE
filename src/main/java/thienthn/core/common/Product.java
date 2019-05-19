package thienthn.core.common;

public class Product {
    /**
     * index of product in the product list
     */
    private int index;

    /**
     * name of product
     */
    private String productName;

    /**
     * grade of product based on algorithm
     * we using this property to find which product would be the most related
     * the great grade as the great relation
     */
    private double grade;

    /**
     * @param index       index of product
     * @param productName name of product
     */
    public Product(int index, String productName) {
        this.index = index;
        this.productName = productName;
        grade = 0;
    }

    public int getIndex() {
        return index;
    }

    public String getProductName() {
        return productName;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}
