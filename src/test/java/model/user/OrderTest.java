package model.user;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.restaurant.Meal;
import model.restaurant.Restaurant;
import static model.user.Customer.Type.*;
import static org.assertj.core.api.Assertions.*;

class OrderTest
{
    private Restaurant restaurant;
    private Meal       cheapMeal;
    private Meal       expensiveMeal;

    @BeforeEach
    void setUp()
    {
        restaurant = new Restaurant("Test restaurant");
        restaurant.addMeal("Cheap meal",     "Recipe", 1000);
        restaurant.addMeal("Expensive meal", "Recipe", 2000);
        cheapMeal     = restaurant.getMealByName("Cheap meal");
        expensiveMeal = restaurant.getMealByName("Expensive meal");
    }

    @Test
    void getPrice_childProfile_applies50PercentDiscount()
    {
        Customer customer = new Customer("A", "B", CHILD);
        Order order = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(order.getPrice()).isEqualTo(1500);
    }

    @Test
    void getPrice_studentProfile_applies25PercentDiscount()
    {
        Customer customer = new Customer("A", "B", STUDENT);
        Order order = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(order.getPrice()).isEqualTo(2250);
    }

    @Test
    void getPrice_standardProfile_noDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        Order order = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(order.getPrice()).isEqualTo(3000);
    }

    @Test
    void getPrice_5thOrderAtSameRestaurant_applies10PercentDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 4; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Order fifthOrder = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(fifthOrder.getPrice()).isEqualTo(900);
    }

    @Test
    void getPrice_4thOrderAtSameRestaurant_noLoyaltyDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 3; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Order fourthOrder = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(fourthOrder.getPrice()).isEqualTo(1000);
    }

    @Test
    void getPrice_10thOrderOnPlatform_applies15PercentDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 9; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Order tenthOrder = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(tenthOrder.getPrice()).isEqualTo(850);
    }

    @Test
    void getPrice_platformLoyaltyBeatsRestaurantLoyalty()
    {
        Restaurant other = new Restaurant("Other restaurant");
        other.addMeal("Other meal", "Recipe", 1000);
        Meal otherMeal = other.getMealByName("Other meal");

        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 5; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        for (int i = 0; i < 4; i++)
            customer.makeOrder(other, List.of(otherMeal));
        Order tenthOrder = customer.makeOrder(other, List.of(otherMeal));

        assertThat(tenthOrder.getPrice()).isEqualTo(850);
    }

    @Test
    void getPrice_childOn5thRestaurantOrder_profileDiscountWins()
    {
        Customer customer = new Customer("A", "B", CHILD);
        for (int i = 0; i < 4; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Order fifthOrder = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(fifthOrder.getPrice()).isEqualTo(500);
    }

    @Test
    void getPrice_studentOn10thPlatformOrder_profileDiscountWins()
    {
        Customer customer = new Customer("A", "B", STUDENT);
        for (int i = 0; i < 9; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Order tenthOrder = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(tenthOrder.getPrice()).isEqualTo(750);
    }

    @Test
    void getPrice_secondOrderWithinAWeek_cheapestMealIsFree()
    {
        Customer customer = new Customer("A", "B", OTHER);
        customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));
        Order second = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(second.getPrice()).isEqualTo(2000);
    }

    @Test
    void getPrice_secondOrderWithinAWeek_cheapestIsFreeWithDiscount()
    {
        Customer customer = new Customer("A", "B", STUDENT);
        customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));
        Order second = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(second.getPrice()).isEqualTo(1500);
    }

    @Test
    void getPrice_secondOrderWithinAWeek_onlyOneMeal_noFreeDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        customer.makeOrder(restaurant, List.of(cheapMeal));
        Order second = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(second.getPrice()).isEqualTo(1000);
    }

    @Test
    void getPrice_firstOrderEver_noFreeDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        Order order = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(order.getPrice()).isEqualTo(3000);
    }

    @Test
    void makeOrder_emptyMealsList_throws()
    {
        Customer customer = new Customer("A", "B", OTHER);

        assertThatThrownBy(() -> customer.makeOrder(restaurant, List.of()))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
