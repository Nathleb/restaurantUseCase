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
        Purchase purchase = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(purchase.getPrice()).isEqualTo(1500);
    }

    @Test
    void getPrice_studentProfile_applies25PercentDiscount()
    {
        Customer customer = new Customer("A", "B", STUDENT);
        Purchase purchase = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(purchase.getPrice()).isEqualTo(2250);
    }

    @Test
    void getPrice_standardProfile_noDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        Purchase purchase = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(purchase.getPrice()).isEqualTo(3000);
    }

    @Test
    void getPrice_5thOrderAtSameRestaurant_applies10PercentDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 4; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase fifth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(fifth.getPrice()).isEqualTo(900);
    }

    @Test
    void getPrice_4thOrderAtSameRestaurant_noLoyaltyDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 3; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase fourth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(fourth.getPrice()).isEqualTo(1000);
    }

    @Test
    void getPrice_10thOrderOnPlatform_applies15PercentDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 9; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase tenth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(tenth.getPrice()).isEqualTo(850);
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
        Purchase tenth = customer.makeOrder(other, List.of(otherMeal));

        assertThat(tenth.getPrice()).isEqualTo(850);
    }

    @Test
    void getPrice_childOn5thRestaurantOrder_profileDiscountWins()
    {
        Customer customer = new Customer("A", "B", CHILD);
        for (int i = 0; i < 4; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase fifth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(fifth.getPrice()).isEqualTo(500);
    }

    @Test
    void getPrice_studentOn10thPlatformOrder_profileDiscountWins()
    {
        Customer customer = new Customer("A", "B", STUDENT);
        for (int i = 0; i < 9; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase tenth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(tenth.getPrice()).isEqualTo(750);
    }

    @Test
    void getPrice_secondOrderWithinAWeek_cheapestMealIsFree()
    {
        Customer customer = new Customer("A", "B", OTHER);
        customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));
        Purchase second = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(second.getPrice()).isEqualTo(2000);
    }

    @Test
    void getPrice_secondOrderWithinAWeek_cheapestIsFreeWithDiscount()
    {
        Customer customer = new Customer("A", "B", STUDENT);
        customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));
        Purchase second = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(second.getPrice()).isEqualTo(1500);
    }

    @Test
    void getPrice_secondOrderWithinAWeek_onlyOneMeal_noFreeDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase second = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(second.getPrice()).isEqualTo(1000);
    }

    @Test
    void getPrice_firstOrderEver_noFreeDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        Purchase purchase = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(purchase.getPrice()).isEqualTo(3000);
    }

    @Test
    void makeOrder_emptyMealsList_throws()
    {
        Customer customer = new Customer("A", "B", OTHER);

        assertThatThrownBy(() -> customer.makeOrder(restaurant, List.of()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makePurchase_multiRestaurant_sumsOrders()
    {
        Restaurant other = new Restaurant("Other restaurant");
        other.addMeal("Other meal", "Recipe", 1500);
        Meal otherMeal = other.getMealByName("Other meal");

        Customer customer = new Customer("A", "B", OTHER);
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(cheapMeal)),
            new Order(other, List.of(otherMeal))));

        assertThat(purchase.getPrice()).isEqualTo(2500);
    }

    @Test
    void makePurchase_null_throws()
    {
        Customer customer = new Customer("A", "B", OTHER);

        assertThatThrownBy(() -> customer.makePurchase(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void makePurchase_emptyList_throws()
    {
        Customer customer = new Customer("A", "B", OTHER);

        assertThatThrownBy(() -> customer.makePurchase(List.of()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makeOrder_nullMeals_throws()
    {
        Customer customer = new Customer("A", "B", OTHER);

        assertThatThrownBy(() -> customer.makeOrder(restaurant, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void order_nullRestaurant_throws()
    {
        assertThatThrownBy(() -> new Order(null, List.of(cheapMeal)))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void order_nullMeals_throws()
    {
        assertThatThrownBy(() -> new Order(restaurant, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getPrice_multiRestaurant_restaurantLoyaltyIsolatedPerOrder()
    {
        Restaurant other = new Restaurant("Other restaurant");
        other.addMeal("Other meal", "Recipe", 1000);
        Meal otherMeal = other.getMealByName("Other meal");

        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 4; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));

        // 5ème commande restaurant — loyalty s'applique à cheapMeal mais PAS à otherMeal
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(cheapMeal)),
            new Order(other, List.of(otherMeal))));

        assertThat(purchase.getPrice()).isEqualTo(900 + 1000); // 10% sur cheapMeal seulement
    }

    @Test
    void getPrice_multiRestaurant_freeMealCrossRestaurant()
    {
        Restaurant other = new Restaurant("Other restaurant");
        other.addMeal("Cheap other", "Recipe", 500);
        Meal cheapOther = other.getMealByName("Cheap other");

        Customer customer = new Customer("A", "B", OTHER);
        customer.makeOrder(restaurant, List.of(cheapMeal));

        // Le meal le moins cher (cheapOther à 500) est dans l'autre restaurant
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(expensiveMeal)),
            new Order(other, List.of(cheapOther))));

        assertThat(purchase.getPrice()).isEqualTo(2000); // cheapOther offert, expensiveMeal plein prix
    }
}
