package org.beverageproducer.models;

import org.beverageproducer.exceptions.InvalidCountOfItemsException;
import org.beverageproducer.exceptions.InvalidPercentException;
import org.beverageproducer.exceptions.NegativeCountOfItemsException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;

public class Client implements IClient {
    private final int id;
    private int basicClientDiscountPercent;
    private int above10000Percent;
    private int above30000Percent;
    private Map<Product, Integer> orderedProducts;

    public Client(int id, int basicClientDiscountPercent, int above10000Percent, int above30000Percent) throws InvalidPercentException {
        this.id = id;
        setBasicClientDiscountPercent(basicClientDiscountPercent);
        setAbove10000Percent(above10000Percent);
        setAbove30000Percent(above30000Percent);
        orderedProducts = new TreeMap<>();
    }

    public int getId() {
        return id;
    }

    public int getBasicClientDiscountPercent() {
        return basicClientDiscountPercent;
    }

    public int getAbove10000Percent() {
        return above10000Percent;
    }

    public int getAbove30000Percent() {
        return above30000Percent;
    }

    public Map<Product, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    public void setBasicClientDiscountPercent(int basicClientDiscountPercent) throws InvalidPercentException {
        if (basicClientDiscountPercent < 0) {
            throw new InvalidPercentException();
        }
        this.basicClientDiscountPercent = basicClientDiscountPercent;
    }

    public void setAbove10000Percent(int above10000Percent) throws InvalidPercentException {
        if (above10000Percent < 0) {
            throw new InvalidPercentException();
        }
        this.above10000Percent = above10000Percent;
    }

    public void setAbove30000Percent(int above30000Percent) throws InvalidPercentException {
        //10000 discount can't be more than 30000 discount
        if (above30000Percent < 0 || above30000Percent < this.above10000Percent) {
            throw new InvalidPercentException();
        }
        this.above30000Percent = above30000Percent;
    }

    @Override
    public boolean addOrderedProduct(Product product, int quantity) throws InvalidCountOfItemsException {
        if (quantity <= 0) {
            throw new InvalidCountOfItemsException();
        }
        orderedProducts.put(product, quantity);
        return true;
    }

    @Override
    public BigDecimal sumOfOrders() throws NegativeCountOfItemsException {
        BigDecimal totalSum = BigDecimal.valueOf(0);

        for (Map.Entry<Product, Integer> entry : orderedProducts.entrySet()) {
            Product key = entry.getKey();
            Integer value = entry.getValue();
            totalSum = totalSum.add(key.calculatedPrice(value));
        }
        return totalSum.setScale(2, RoundingMode.UP);
    }

    @Override
    public BigDecimal priceWithBasicDiscount() throws NegativeCountOfItemsException {
        return sumOfOrders().subtract(sumOfOrders().multiply(BigDecimal.valueOf(basicClientDiscountPercent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.UP));
    }

    @Override
    public BigDecimal priceWithAdditionalDiscount() throws NegativeCountOfItemsException {
        int sumOfQuantities = 0;
        for (Map.Entry<Product, Integer> entry : orderedProducts.entrySet()) {
            Product key = entry.getKey();
            Integer value = entry.getValue();
            sumOfQuantities += value;
        }
        if (sumOfQuantities < 10000) return priceWithBasicDiscount();
        else if (sumOfQuantities < 30000) {
            return priceWithBasicDiscount().subtract(priceWithBasicDiscount().multiply(BigDecimal.valueOf(above10000Percent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.UP));
        } else {
            return priceWithBasicDiscount().subtract(priceWithBasicDiscount().multiply(BigDecimal.valueOf(above30000Percent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.UP));
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", basicClientDiscountPercent=" + basicClientDiscountPercent +
                ", above10000Percent=" + above10000Percent +
                ", above30000Percent=" + above30000Percent +
                ", orderedProducts=" + orderedProducts +
                '}';
    }
}
