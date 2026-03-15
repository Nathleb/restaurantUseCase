package model.restaurant;

import java.util.Objects;

import lombok.Getter;
import model.Named;

public class Meal implements Named
{
    private final Restaurant restaurant;

    @Getter
    private final String name;

    @Getter
    private final String recipe;

    @Getter
    private final int price;

    Meal(Restaurant restaurant, String name, String recipe, int price)
    {
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant must not be null");
        this.name       = Objects.requireNonNull(name,       "name must not be null");
        this.recipe     = Objects.requireNonNull(recipe,     "recipe must not be null");
        if (price <= 0)
            throw new IllegalArgumentException("price must be positive");
        this.price = price;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Meal meal = (Meal) o;
        return Objects.equals(name, meal.name) && Objects.equals(restaurant, meal.restaurant);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, restaurant);
    }
}
