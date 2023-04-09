package org.beverageproducer.models;

import org.beverageproducer.enums.MarkupType;
import org.beverageproducer.enums.PromotionType;
import org.beverageproducer.exceptions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Product implements IProduct, Comparable<Product> {
    private final char name;
    private BigDecimal unitCost;
    //two types of markup - examples: 80%, 0.9EUR/unit
    private MarkupType markupType;
    private BigDecimal markup;
    private PromotionType promotionType;
    //next 3 variables are used for promotion calculation
    //they can be easily changed in future
    private int percentPromotionDiscount;
    private int toBuyForPromotionDiscount;
    //you can get X free for example, but could also get 1 free for X bought
    private int freeItemsFromPromotionDiscount;

    public Product(char name, BigDecimal unitCost, MarkupType markupType, BigDecimal markup)
            throws InvalidMarkupException, InvalidUnitCostException {
        this.name = name;
        setUnitCost(unitCost);
        this.markupType = markupType;
        setMarkup(markup);
        this.promotionType = PromotionType.NONE;
        this.percentPromotionDiscount = 0;
        this.toBuyForPromotionDiscount = 0;
        this.freeItemsFromPromotionDiscount = 0;
    }

    public char getName() {
        return name;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public BigDecimal getMarkup() {
        return markup;
    }

    public PromotionType getPromotionType() {
        return promotionType;
    }

    public MarkupType getMarkupType() {
        return markupType;
    }

    public int getPercentPromotionDiscount() {
        return percentPromotionDiscount;
    }

    public int getToBuyForPromotionDiscount() {
        return toBuyForPromotionDiscount;
    }

    public int getFreeItemsFromPromotionDiscount() {
        return freeItemsFromPromotionDiscount;
    }

    public void setUnitCost(BigDecimal unitCost) throws InvalidUnitCostException {
        if (unitCost.compareTo(BigDecimal.valueOf(0)) < 1) {
            throw new InvalidUnitCostException();
        }
        this.unitCost = unitCost;
    }

    public void setMarkupType(MarkupType markupType) {
        this.markupType = markupType;
    }

    public void setMarkup(BigDecimal markup) throws InvalidMarkupException {
        if (markup.compareTo(BigDecimal.valueOf(0)) < 1) {
            throw new InvalidMarkupException();
        }
        this.markup = markup;
    }

    @Override
    public boolean addPercentPromotion(int percent)
            throws InvalidPercentException {
        if (percent <= 0) {
            throw new InvalidPercentException();
        }
        this.promotionType = PromotionType.PERCENT;
        this.percentPromotionDiscount = percent;
        return true;
    }

    @Override
    public boolean addFreeItemsPromotion(int toBuy, int freeItems)
            throws InvalidCountOfItemsException {
        if (toBuy < 1 || freeItems < 1) {
            throw new InvalidCountOfItemsException();
        }
        this.promotionType = PromotionType.GET_FREE_ITEMS;
        this.toBuyForPromotionDiscount = toBuy;
        this.freeItemsFromPromotionDiscount = freeItems;
        return true;
    }

    @Override
    public boolean removePromotion()
            throws NoPromotionToRemoveException {
        if (promotionType.equals(PromotionType.NONE)) {
            throw new NoPromotionToRemoveException();
        }
        this.promotionType = PromotionType.NONE;
        this.toBuyForPromotionDiscount = 0;
        this.freeItemsFromPromotionDiscount = 0;
        this.percentPromotionDiscount = 0;
        return true;
    }

    @Override
    public BigDecimal calculatedPrice(int quantityOfItems) throws NegativeCountOfItemsException {
        if (quantityOfItems < 0) {
            throw new NegativeCountOfItemsException();
        } else if (quantityOfItems == 0) {
            return BigDecimal.valueOf(0);
        }
        return calculatedPricePerItem(quantityOfItems).multiply(BigDecimal.valueOf(quantityOfItems));
    }

    @Override
    public BigDecimal calculatedPricePerItem(int quantityOfItems) throws NegativeCountOfItemsException {
        if (quantityOfItems < 0) {
            throw new NegativeCountOfItemsException();
        } else if (quantityOfItems == 0) {
            return BigDecimal.valueOf(0);
        }
        BigDecimal finalPrice;
        if (markupType == MarkupType.PERCENT) {
            finalPrice = unitCost.add(unitCost.multiply(markup).divide(BigDecimal.valueOf(100)));
        } else finalPrice = unitCost.add(markup);

        if (promotionType.equals(PromotionType.GET_FREE_ITEMS)) {
            int paidItems = quantityOfItems - (quantityOfItems / (toBuyForPromotionDiscount + freeItemsFromPromotionDiscount));
            finalPrice = finalPrice.multiply(BigDecimal.valueOf(paidItems)).divide(BigDecimal.valueOf(quantityOfItems), 2, RoundingMode.HALF_EVEN);

        } else if (promotionType.equals(PromotionType.PERCENT)) {
            finalPrice = finalPrice.subtract(finalPrice.multiply(BigDecimal.valueOf(percentPromotionDiscount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN)));
        }
        return finalPrice;
    }

    @Override
    public int compareTo(Product o) {
        return Character.compare(this.name, o.name);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name=" + name +
                ", unitCost=" + unitCost +
                ", markupType=" + markupType +
                ", markup=" + markup +
                ", promotionType=" + promotionType +
                '}';
    }
}
