package model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.restaurant.Restaurant;

import static org.assertj.core.api.Assertions.*;

class RestaurantOwnerTest
{
    private Restaurant      restaurant;
    private RestaurantOwner owner;

    @BeforeEach
    void setUp()
    {
        restaurant = new Restaurant("Le Ticino");
        owner = new RestaurantOwner("Alice", "Martin", restaurant);
    }

    @Test
    void addMeal_addsMealToRestaurant()
    {
        owner.addMeal("Burger", "Recipe", 1200);

        assertThat(restaurant.getMeals()).hasSize(1);
    }

    @Test
    void addMeal_duplicateName_throws()
    {
        owner.addMeal("Burger", "Recipe", 1200);

        assertThatThrownBy(() -> owner.addMeal("Burger", "Other recipe", 1500))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addMeal_negativePrice_throws()
    {
        assertThatThrownBy(() -> owner.addMeal("Burger", "Recipe", -100))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
