package model.restaurant;

import java.util.Objects;

import lombok.Getter;
import model.Entity;

public class Meal implements Entity
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
        this.restaurant = restaurant;
        this.name = name;
        this.recipe = recipe;
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
