package org.beverageproducer.service;

import org.beverageproducer.enums.MarkupType;
import org.beverageproducer.enums.PromotionType;
import org.beverageproducer.exceptions.*;
import org.beverageproducer.models.Client;
import org.beverageproducer.models.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;

public class Controller {
    private final Map<Integer, Client> clients;
    private final Map<Character, Product> products;

    public Controller() {
        clients = new TreeMap<>();
        products = new TreeMap<>();
    }

    public Map<Integer, Client> getClients() {
        return clients;
    }

    public Map<Character, Product> getProducts() {
        return products;
    }

    public void createClientsAndProducts()
            throws InvalidMarkupException, InvalidUnitCostException, InvalidPercentException, InvalidCountOfItemsException {
        Product productA = new Product('A', BigDecimal.valueOf(0.52), MarkupType.PERCENT, BigDecimal.valueOf(80));
        Product productB = new Product('B', BigDecimal.valueOf(0.38), MarkupType.PERCENT, BigDecimal.valueOf(120));
        Product productC = new Product('C', BigDecimal.valueOf(0.41), MarkupType.PRICE_PER_UNIT, BigDecimal.valueOf(0.9));
        Product productD = new Product('D', BigDecimal.valueOf(0.60), MarkupType.PRICE_PER_UNIT, BigDecimal.valueOf(1));
        Client client1 = new Client(1, 5, 0, 2);
        Client client2 = new Client(2, 4, 1, 2);
        Client client3 = new Client(3, 3, 1, 3);
        Client client4 = new Client(4, 2, 3, 5);
        Client client5 = new Client(5, 0, 5, 7);
        productB.addPercentPromotion(30);
        productD.addFreeItemsPromotion(2, 1);
        clients.put(1, client1);
        clients.put(2, client2);
        clients.put(3, client3);
        clients.put(4, client4);
        clients.put(5, client5);
        products.put('A', productA);
        products.put('B', productB);
        products.put('C', productC);
        products.put('D', productD);
    }


    public void startApplication(int clientID, int quantityA, int quantityB, int quantityC, int quantityD)
            throws InvalidMarkupException, InvalidPercentException, InvalidUnitCostException, InvalidCountOfItemsException, NegativeCountOfItemsException {
        createClientsAndProducts();
        Map<Product, Integer> appProducts = clients.get(clientID).getOrderedProducts();
        clients.get(clientID).getOrderedProducts().put(products.get('A'), quantityA);
        clients.get(clientID).getOrderedProducts().put(products.get('B'), quantityB);
        clients.get(clientID).getOrderedProducts().put(products.get('C'), quantityC);
        clients.get(clientID).getOrderedProducts().put(products.get('D'), quantityD);
        for (Map.Entry<Product, Integer> entry : appProducts.entrySet()) {
            Product key = entry.getKey();
            Integer value = entry.getValue();
            if (value > 0) {
                System.out.print(key.getName() + " {");
                System.out.print(value + ", " + key.getUnitCost().setScale(2, RoundingMode.UP) + ", ");
                if (!key.getPromotionType().equals(PromotionType.NONE)) {
                    try {
                        System.out.print(key.calculatedPricePerItem(value).setScale(2, RoundingMode.UP) + ", ");
                    } catch (NegativeCountOfItemsException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    System.out.println(key.calculatedPrice(value).setScale(2, RoundingMode.UP) + "}");
                } catch (NegativeCountOfItemsException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("Total amount before discounts: EUR " + clients.get(clientID).sumOfOrders());
        System.out.println("Price with basic client discount: EUR " + clients.get(clientID).priceWithBasicDiscount());
        System.out.println("Price with additional client discount: EUR " + clients.get(clientID).priceWithAdditionalDiscount());
    }
}
