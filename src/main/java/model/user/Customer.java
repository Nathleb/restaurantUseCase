package model.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import model.RestaurantSale;
import model.pricing.DiscountRuleRegistry;
import model.restaurant.Meal;
import model.restaurant.Order;
import model.restaurant.Restaurant;

public class Customer implements User
{
    @Getter
    private final String firstName;

    @Getter
    private final String lastName;

    @Getter
    private final Type type;

    private final List<Purchase> purchases;

    public Customer(String firstName, String lastName, Type type)
    {
        this.firstName = Objects.requireNonNull(firstName, "firstName must not be null");
        this.lastName  = Objects.requireNonNull(lastName,  "lastName must not be null");
        this.type      = Objects.requireNonNull(type,      "type must not be null");
        this.purchases = new ArrayList<>();
    }

    public List<Purchase> getPurchases()
    {
        return Collections.unmodifiableList(purchases);
    }

    // Avis sur le breaking change Order n'a plus la meme signification, on retourne ici Purchase
    public Purchase makeOrder(Restaurant restaurant, List<Meal> meals)
    {
        Objects.requireNonNull(restaurant, "restaurant must not be null");
        Objects.requireNonNull(meals,      "meals must not be null");
        return makePurchase(List.of(new Order(restaurant, meals)));
    }

    public Purchase makePurchase(List<Order> orders)
    {
        Objects.requireNonNull(orders, "orders must not be null");
        if (orders.isEmpty())
            throw new IllegalArgumentException("at least one order is required");

        Purchase purchase = new Purchase(this, orders, DiscountRuleRegistry.rules());
        purchases.add(purchase);
        // En pratique ce sera une vue sur purchase en base donc pas de transaction multiples
        orders.forEach(order -> order.getRestaurant()
            .registerSale(new RestaurantSale(getName(), purchase.getDate(), order.getMeals())));
        return purchase;
    }

    public enum Type
    {
        CHILD,
        STUDENT,
        OTHER
    }
}
