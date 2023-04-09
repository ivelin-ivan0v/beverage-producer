package org.beverageproducer.models;

import org.beverageproducer.exceptions.InvalidCountOfItemsException;
import org.beverageproducer.exceptions.NegativeCountOfItemsException;

import java.math.BigDecimal;

public interface IClient {
    boolean addOrderedProduct(Product product, int quantity) throws InvalidCountOfItemsException;

    BigDecimal sumOfOrders() throws NegativeCountOfItemsException;

    BigDecimal priceWithBasicDiscount() throws NegativeCountOfItemsException;

    BigDecimal priceWithAdditionalDiscount() throws NegativeCountOfItemsException;
}
