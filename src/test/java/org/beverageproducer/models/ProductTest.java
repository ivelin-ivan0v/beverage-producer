package org.beverageproducer.models;

import org.beverageproducer.enums.MarkupType;
import org.beverageproducer.enums.PromotionType;
import org.beverageproducer.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;
    private Product product2;

    @BeforeEach
    void unit() throws InvalidMarkupException, InvalidUnitCostException {
        product = new Product('A', new BigDecimal(10), MarkupType.PRICE_PER_UNIT, new BigDecimal(10));
        product2 = new Product('B', new BigDecimal(10), MarkupType.PERCENT, new BigDecimal(20));
    }

    @Test
    void setUnitCost() {
        assertThrows(InvalidUnitCostException.class, () -> product.setUnitCost(new BigDecimal(-1)));
        assertThrows(InvalidUnitCostException.class, () -> product.setUnitCost(new BigDecimal(-100)));
        assertThrows(InvalidUnitCostException.class, () -> product.setUnitCost(new BigDecimal(0)));
        assertDoesNotThrow(() -> product.setUnitCost(new BigDecimal(1)));
        assertDoesNotThrow(() -> product.setUnitCost(new BigDecimal(100)));
    }

    @Test
    void setMarkup() {
        assertThrows(InvalidMarkupException.class, () -> product.setMarkup(new BigDecimal(-1)));
        assertThrows(InvalidMarkupException.class, () -> product.setMarkup(new BigDecimal(0)));
        assertDoesNotThrow(() -> product.setMarkup(new BigDecimal(1)));
        assertDoesNotThrow(() -> product.setMarkup(new BigDecimal(100)));
    }

    @Test
    void addPercentPromotion() {
        assertThrows(InvalidPercentException.class, () -> product.addPercentPromotion(-1));
        assertThrows(InvalidPercentException.class, () -> product.addPercentPromotion(0));
        assertDoesNotThrow(() -> product.addPercentPromotion(1));
        assertEquals(PromotionType.PERCENT, product.getPromotionType());
        assertDoesNotThrow(() -> product.addPercentPromotion(5));
        assertEquals(5, product.getPercentPromotionDiscount());
    }

    @Test
    void addFreeItemsPromotion() {
        assertThrows(InvalidCountOfItemsException.class, () -> product.addFreeItemsPromotion(-1, -1));
        assertThrows(InvalidCountOfItemsException.class, () -> product.addFreeItemsPromotion(1, -1));
        assertThrows(InvalidCountOfItemsException.class, () -> product.addFreeItemsPromotion(-1, 1));
        assertThrows(InvalidCountOfItemsException.class, () -> product.addFreeItemsPromotion(0, 5));
        assertEquals(PromotionType.NONE, product.getPromotionType());
        assertDoesNotThrow(() -> product.addFreeItemsPromotion(1, 1));
        assertDoesNotThrow(() -> product.addFreeItemsPromotion(2, 1));
        assertDoesNotThrow(() -> product.addFreeItemsPromotion(2, 3));
        assertEquals(PromotionType.GET_FREE_ITEMS, product.getPromotionType());
    }

    @Test
    void removePromotion() throws InvalidPercentException {
        assertThrows(NoPromotionToRemoveException.class, () -> product.removePromotion());
        product.addPercentPromotion(10);
        assertEquals(PromotionType.PERCENT, product.getPromotionType());
        assertDoesNotThrow(() -> product.removePromotion());
        assertEquals(PromotionType.NONE, product.getPromotionType());
    }


    @Test
    void calculatedPrice() throws NegativeCountOfItemsException {
        assertThrows(NegativeCountOfItemsException.class, () -> product.calculatedPrice(-1));
        assertEquals(new BigDecimal(0), product.calculatedPrice(0));
    }

    @Test
    void calculatedPricePerItemWithPercentMarkup() throws NegativeCountOfItemsException, InvalidPercentException {
        assertThrows(NegativeCountOfItemsException.class, () -> product2.calculatedPricePerItem(-1));
        assertEquals(new BigDecimal(0), product2.calculatedPricePerItem(0));
        assertEquals(new BigDecimal(12), product2.calculatedPricePerItem(10));
        product2.addPercentPromotion(50);
        assertEquals(new BigDecimal("6.00"), product2.calculatedPricePerItem(11));
    }

    @Test
    void calculatedPricePerItemWithPricePerUnitMarkup() throws NegativeCountOfItemsException, InvalidCountOfItemsException {
        assertThrows(NegativeCountOfItemsException.class, () -> product.calculatedPricePerItem(-1));
        assertEquals(new BigDecimal(0), product.calculatedPricePerItem(0));
        assertEquals(new BigDecimal(20), product.calculatedPricePerItem(10));
        product.addFreeItemsPromotion(1, 1);
        assertEquals(new BigDecimal("10.00"), product.calculatedPricePerItem(10));
        product.addFreeItemsPromotion(2, 3);
        assertEquals(new BigDecimal("16.00"), product.calculatedPricePerItem(10));
    }
}