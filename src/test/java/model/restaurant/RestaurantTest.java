package model.restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RestaurantTest
{
    private Restaurant restaurant;

    @BeforeEach
    void setUp()
    {
        restaurant = new Restaurant("Test restaurant");
    }

    @Test
    void addMeal_addsToMenu()
    {
        restaurant.addMeal("Burger", "Recipe", 1200);

        assertThat(restaurant.getMeals()).hasSize(1);
        assertThat(restaurant.getMeals().get(0).getName()).isEqualTo("Burger");
    }

    @Test
    void addMeal_duplicateName_throws()
    {
        restaurant.addMeal("Burger", "Recipe", 1200);

        assertThatThrownBy(() -> restaurant.addMeal("Burger", "Other recipe", 1500))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getMealByName_found_returnsMeal()
    {
        restaurant.addMeal("Burger", "Recipe", 1200);

        assertThat(restaurant.getMealByName("Burger").getName()).isEqualTo("Burger");
    }

    @Test
    void getMealByName_notFound_throws()
    {
        assertThatThrownBy(() -> restaurant.getMealByName("Unknown"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getMeals_returnsUnmodifiableList()
    {
        restaurant.addMeal("Burger", "Recipe", 1200);

        assertThatThrownBy(() -> restaurant.getMeals().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void addMeal_zeroPice_throws()
    {
        assertThatThrownBy(() -> restaurant.addMeal("Burger", "Recipe", 0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addMeal_negativePrice_throws()
    {
        assertThatThrownBy(() -> restaurant.addMeal("Burger", "Recipe", -100))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
