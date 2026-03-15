package model.restaurant;

import java.util.List;
import java.util.Objects;

import lombok.Getter;

public class Order
{
    @Getter
    private final Restaurant restaurant;

    @Getter
    private final List<Meal> meals;

    public Order(Restaurant restaurant, List<Meal> meals)
    {
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant must not be null");
        Objects.requireNonNull(meals, "meals must not be null");
        if (meals.isEmpty())
            throw new IllegalArgumentException("meals must not be empty");
        this.meals = List.copyOf(meals);
    }

    public int getRawPrice()
    {
        return meals.stream().mapToInt(Meal::getPrice).sum();
    }
}
