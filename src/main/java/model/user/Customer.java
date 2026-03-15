package model.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import model.restaurant.Meal;
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
        if (meals.isEmpty())
            throw new IllegalArgumentException("meals must not be empty");
        Order order = new Order(restaurant, this, meals);
        orders.add(order);
        restaurant.registerOrder(order);
        return order;
    }

    public enum Type
    {
        CHILD,
        STUDENT,
        OTHER
    }
}
