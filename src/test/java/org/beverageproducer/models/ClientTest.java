package org.beverageproducer.models;

import org.beverageproducer.enums.MarkupType;
import org.beverageproducer.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private Client client;
    private Client client2;

    @BeforeEach
    void unit() throws InvalidPercentException {
        client = new Client(1, 20, 0, 0);
        client2 = new Client(1, 0, 5, 5);
    }

    @Test
    void setBasicClientDiscountPercent() {
        assertDoesNotThrow(() -> client.setBasicClientDiscountPercent(0));
        assertDoesNotThrow(() -> client.setBasicClientDiscountPercent(1));
        assertDoesNotThrow(() -> client.setBasicClientDiscountPercent(99));
        assertThrows(InvalidPercentException.class, () -> client.setBasicClientDiscountPercent(-1));
        assertThrows(InvalidPercentException.class, () -> client.setBasicClientDiscountPercent(-100));
    }

    @Test
    void setAbove10000Percent() {
        assertDoesNotThrow(() -> client.setAbove10000Percent(0));
        assertDoesNotThrow(() -> client.setAbove10000Percent(1));
        assertDoesNotThrow(() -> client.setAbove10000Percent(99));
        assertThrows(InvalidPercentException.class, () -> client.setAbove10000Percent(-1));
        assertThrows(InvalidPercentException.class, () -> client.setAbove10000Percent(-100));
    }

    @Test
    void setAbove30000Percent() {
        assertDoesNotThrow(() -> client.setAbove30000Percent(0));
        assertDoesNotThrow(() -> client.setAbove30000Percent(50));
        assertDoesNotThrow(() -> client.setAbove30000Percent(1));
        assertThrows(InvalidPercentException.class, () -> client.setAbove30000Percent(-1));
        assertThrows(InvalidPercentException.class, () -> client.setAbove30000Percent(-100));
    }

    @Test
    void addOrderedProduct() {
        assertThrows(InvalidCountOfItemsException.class, () -> client.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PERCENT, new BigDecimal(1)), 0));
        assertThrows(InvalidCountOfItemsException.class, () -> client.addOrderedProduct(new Product('B', new BigDecimal(1), MarkupType.PERCENT, new BigDecimal(1)), -1));
        assertThrows(InvalidCountOfItemsException.class, () -> client.addOrderedProduct(new Product('C', new BigDecimal(1), MarkupType.PERCENT, new BigDecimal(1)), -100));
    }

    @Test
    void sumOfOrders() throws NegativeCountOfItemsException, InvalidCountOfItemsException, InvalidMarkupException, InvalidUnitCostException {
        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.UP), client.sumOfOrders());
        client.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal(2).setScale(2, RoundingMode.UP), client.sumOfOrders());
        client.addOrderedProduct(new Product('B', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal(4).setScale(2, RoundingMode.UP), client.sumOfOrders());
    }

    @Test
    void priceWithBasicDiscount() throws NegativeCountOfItemsException, InvalidCountOfItemsException, InvalidMarkupException, InvalidUnitCostException {
        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.UP), client2.priceWithBasicDiscount());
        client2.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal(2).setScale(2, RoundingMode.UP), client2.priceWithBasicDiscount());
        client2.addOrderedProduct(new Product('B', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal(4).setScale(2, RoundingMode.UP), client2.priceWithBasicDiscount());

        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
        client.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal("1.6").setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
        client.addOrderedProduct(new Product('B', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal("3.2").setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
    }

    @Test
    void priceWithAdditionalDiscountLessThan10k() throws NegativeCountOfItemsException, InvalidCountOfItemsException, InvalidMarkupException, InvalidUnitCostException {
        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.UP), client2.priceWithAdditionalDiscount());
        client2.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal(2).setScale(2, RoundingMode.UP), client2.priceWithAdditionalDiscount());
        client2.addOrderedProduct(new Product('B', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal(4).setScale(2, RoundingMode.UP), client2.priceWithAdditionalDiscount());

        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
        client.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal("1.6").setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
        client.addOrderedProduct(new Product('B', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 1);
        assertEquals(new BigDecimal("3.2").setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
    }

    @Test
    void priceWithAdditionalDiscountOver30k() throws NegativeCountOfItemsException, InvalidCountOfItemsException, InvalidMarkupException, InvalidUnitCostException {
        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.UP), client2.priceWithAdditionalDiscount());
        client2.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 15000);
        assertEquals(new BigDecimal(28500).setScale(2, RoundingMode.UP), client2.priceWithAdditionalDiscount());

        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
        client.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 30000);
        assertEquals(new BigDecimal(48000).setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
        client.addOrderedProduct(new Product('B', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 10000);
        assertEquals(new BigDecimal(64000).setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
    }

    @Test
    void priceWithAdditionalDiscountFrom10kTo30k() throws NegativeCountOfItemsException, InvalidCountOfItemsException, InvalidMarkupException, InvalidUnitCostException {
        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.UP), client2.priceWithAdditionalDiscount());
        client2.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 15000);
        assertEquals(new BigDecimal(28500).setScale(2, RoundingMode.UP), client2.priceWithAdditionalDiscount());

        assertEquals(new BigDecimal(0).setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
        client.addOrderedProduct(new Product('A', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 30000);
        assertEquals(new BigDecimal(48000).setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
        client.addOrderedProduct(new Product('B', new BigDecimal(1), MarkupType.PRICE_PER_UNIT, new BigDecimal(1)), 10000);
        assertEquals(new BigDecimal(64000).setScale(2, RoundingMode.UP), client.priceWithAdditionalDiscount());
    }
}