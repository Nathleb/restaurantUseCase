package model.restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import model.Named;
import model.RestaurantSale;
import static java.lang.String.format;

public class Restaurant implements Named
{
    @Getter
    private final String name;

    private final List<Meal>           meals = new ArrayList<>();
    private final List<RestaurantSale> sales = new ArrayList<>();

    public Restaurant(String name)
    {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public List<Meal> getMeals()
    {
        return Collections.unmodifiableList(meals);
    }

    public List<RestaurantSale> getSales()
    {
        return Collections.unmodifiableList(sales);
    }

    public void registerSale(RestaurantSale sale)
    {
        Objects.requireNonNull(sale, "sale must not be null");
        sales.add(sale);
    }

    public void addMeal(String mealName, String recipe, int price)
    {
        if (meals.stream().map(Meal::getName).anyMatch(n -> n.equals(mealName)))
            throw new IllegalArgumentException(format("Meal %s already exists in %s", mealName, name));
        meals.add(new Meal(this, mealName, recipe, price));
    }

    // remove et update des meals

    public Meal getMealByName(String mealName)
    {
        return meals.stream()
                    .filter(meal -> meal.getName().equals(mealName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(format("No meal named %s in %s", mealName, name)));
    }
}
