package model.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import model.RestaurantSale;
import model.pricing.DiscountRuleRegistry;
import model.restaurant.Meal;
import model.restaurant.RestaurantOrder;
import model.restaurant.Restaurant;

public class Customer implements User
{
    @Getter
    private final String firstName;

    @Getter
    private final String lastName;

    @Getter
    private final Type type;

    private final List<Order> orders;

    public Customer(String firstName, String lastName, Type type)
    {
        this.firstName = Objects.requireNonNull(firstName, "firstName must not be null");
        this.lastName  = Objects.requireNonNull(lastName,  "lastName must not be null");
        this.type      = Objects.requireNonNull(type,      "type must not be null");
        this.orders = new ArrayList<>();
    }

    public List<Order> getOrders()
    {
        return Collections.unmodifiableList(orders);
    }

    public Order makeOrder(Restaurant restaurant, List<Meal> meals)
    {
        Objects.requireNonNull(restaurant, "restaurant must not be null");
        Objects.requireNonNull(meals,      "meals must not be null");
        return makeOrder(List.of(new RestaurantOrder(restaurant, meals)));
    }

    public Order makeOrder(List<RestaurantOrder> restaurantOrders)
    {
        Objects.requireNonNull(restaurantOrders, "restaurantOrders must not be null");
        if (restaurantOrders.isEmpty())
            throw new IllegalArgumentException("at least one order is required");

        Order order = new Order(this, restaurantOrders, DiscountRuleRegistry.rules());
        orders.add(order);
        order.initPrice();
        // En pratique ce sera une vue sur order en base donc pas de transaction multiples
        restaurantOrders.forEach(restaurantOrder -> restaurantOrder.getRestaurant()
            .registerSale(new RestaurantSale(getName(), order.getDate(), restaurantOrder.getMeals())));
        return order;
    }

    public enum Type
    {
        CHILD,
        STUDENT,
        OTHER
    }
}
