package org.beverageproducer.models;

import org.beverageproducer.exceptions.InvalidCountOfItemsException;
import org.beverageproducer.exceptions.InvalidPercentException;
import org.beverageproducer.exceptions.NegativeCountOfItemsException;
import org.beverageproducer.exceptions.NoPromotionToRemoveException;

import java.math.BigDecimal;

public interface IProduct {
    boolean addPercentPromotion(int percent) throws InvalidPercentException;

    boolean addFreeItemsPromotion(int toBuy, int freeItems) throws InvalidCountOfItemsException;

    boolean removePromotion() throws NoPromotionToRemoveException;

    BigDecimal calculatedPrice(int quantityOfItems) throws NegativeCountOfItemsException;

    BigDecimal calculatedPricePerItem(int quantityOfItems) throws NegativeCountOfItemsException;
}
