package model.user;

import java.util.Objects;

import lombok.Getter;
import model.restaurant.Restaurant;

public class RestaurantOwner implements User
{
    @Getter
    private final String firstName;

    @Getter
    private final String lastName;

    @Getter
    private final Restaurant restaurant;

    public RestaurantOwner(String firstName, String lastName, Restaurant restaurant)
    {
        this.firstName  = Objects.requireNonNull(firstName,  "firstName must not be null");
        this.lastName   = Objects.requireNonNull(lastName,   "lastName must not be null");
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant must not be null");
    }

    public void addMeal(String mealName, String recipe, int price)
    {
        restaurant.addMeal(mealName, recipe, price);
    }

    // remove et update meal
}
