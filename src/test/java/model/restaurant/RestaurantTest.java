package model.restaurant;

import java.util.List;

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
    void addMeal_zeroPrice_throws()
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

    @Test
    void addMeal_nullName_throws()
    {
        assertThatThrownBy(() -> restaurant.addMeal(null, "Recipe", 1200))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void addMeal_nullRecipe_throws()
    {
        assertThatThrownBy(() -> restaurant.addMeal("Burger", null, 1200))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void equals_sameName_consideredEqual()
    {
        assertThat(new Restaurant("Le Ticino")).isEqualTo(new Restaurant("Le Ticino"));
    }

    @Test
    void equals_differentName_notEqual()
    {
        assertThat(new Restaurant("Le Ticino")).isNotEqualTo(new Restaurant("L'étoile"));
    }

    @Test
    void getSales_registeredAfterOrder()
    {
        restaurant.addMeal("Burger", "Recipe", 1200);
        Meal burger = restaurant.getMealByName("Burger");
        model.user.Customer customer = new model.user.Customer("A", "B", model.user.Customer.Type.OTHER);

        customer.makeOrder(restaurant, List.of(burger));

        assertThat(restaurant.getSales()).hasSize(1);
        assertThat(restaurant.getSales().get(0).customerName()).isEqualTo("A B");
        assertThat(restaurant.getSales().get(0).meals()).containsExactly(burger);
    }
}
