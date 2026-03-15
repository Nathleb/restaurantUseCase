package model.restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import model.Entity;
import model.user.Order;
import static java.lang.String.format;

public class Restaurant implements Entity
{
    @Getter
    private final String name;

    private final List<Meal> meals;

    private final List<Order> orders;

    public Restaurant(String name)
    {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.meals = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public List<Meal> getMeals()
    {
        return Collections.unmodifiableList(meals);
    }

    public List<Order> getOrders()
    {
        return Collections.unmodifiableList(orders);
    }

    public void addMeal(String mealName, String recipe, int price)
    {
        Objects.requireNonNull(mealName, "mealName must not be null");
        Objects.requireNonNull(recipe,   "recipe must not be null");
        if (price <= 0)
            throw new IllegalArgumentException("price must be positive");
        if (meals.stream().map(Meal::getName).anyMatch(n -> n.equals(mealName)))
            throw new IllegalArgumentException(format("Meal %s already exists in %s", mealName, name));
        meals.add(new Meal(this, mealName, recipe, price));
    }

    public void registerOrder(Order order)
    {
        Objects.requireNonNull(order, "order must not be null");
        orders.add(order);
    }

    public Meal getMealByName(String mealName)
    {
        return meals.stream()
                    .filter(meal -> meal.getName().equals(mealName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(format("No meal named %s in %s", mealName, name)));
    }
}
