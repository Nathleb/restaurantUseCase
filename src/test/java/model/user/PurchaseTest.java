package model.user;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.restaurant.Meal;
import model.restaurant.Order;
import model.restaurant.Restaurant;
import static model.user.Customer.Type.*;
import static org.assertj.core.api.Assertions.*;

class PurchaseTest
{
    private Restaurant restaurant;
    private Restaurant other;
    private Meal       meal;
    private Meal       otherMeal;

    @BeforeEach
    void setUp()
    {
        restaurant = new Restaurant("Le Ticino");
        restaurant.addMeal("Pizza", "Recipe", 1000);
        meal = restaurant.getMealByName("Pizza");

        other = new Restaurant("L'étoile");
        other.addMeal("Risotto", "Recipe", 1500);
        otherMeal = other.getMealByName("Risotto");
    }

    // --- Validation ---

    @Test
    void makePurchase_nullList_throws()
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
    void order_nullRestaurant_throws()
    {
        assertThatThrownBy(() -> new Order(null, List.of(meal)))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void order_nullMeals_throws()
    {
        assertThatThrownBy(() -> new Order(restaurant, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void order_emptyMeals_throws()
    {
        assertThatThrownBy(() -> new Order(restaurant, List.of()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- Multi-restaurants ---

    @Test
    void getPrice_multiRestaurant_sumsBothOrders()
    {
        Customer customer = new Customer("A", "B", OTHER);
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(meal)),
            new Order(other, List.of(otherMeal))));

        assertThat(purchase.getPrice()).isEqualTo(2500);
    }

    @Test
    void getPrice_multiRestaurant_restaurantLoyaltyAppliesOnlyToItsOrder()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 4; i++)
            customer.makeOrder(restaurant, List.of(meal));

        // 5ème achat chez restaurant → 10% sur restaurant uniquement, pas sur other
        // prendre en compte qu'un meal est offert (trouver une alternative de test)
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(meal, meal)),   // 1000 * 0.90 = 900
            new Order(other, List.of(otherMeal)))); // 1500 * 1.00 = 1500

        assertThat(purchase.getPrice()).isEqualTo(2400);
    }

    @Test
    void getPrice_multiRestaurant_platformLoyaltyAppliesToAllOrders()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 9; i++)
            customer.makeOrder(restaurant, List.of(meal));

        // 10ème achat → 15% sur tous les restaurants
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(meal, meal)),   // 1000 * 0.85 = 850
            new Order(other, List.of(otherMeal)))); // 1500 * 0.85 = 1275

        assertThat(purchase.getPrice()).isEqualTo(2125);
    }

    @Test
    void getPrice_multiRestaurant_childDiscountAppliesToAllOrders()
    {
        Customer customer = new Customer("A", "B", CHILD);
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(meal)),   // 1000 * 0.50 = 500
            new Order(other, List.of(otherMeal)))); // 1500 * 0.50 = 750

        assertThat(purchase.getPrice()).isEqualTo(1250);
    }

    @Test
    void getPrice_multiRestaurant_cheapestMealFreeAcrossRestaurants()
    {
        Customer customer = new Customer("A", "B", OTHER);
        customer.makePurchase(List.of(
            new Order(restaurant, List.of(meal)),
            new Order(other, List.of(otherMeal))));

        // 2ème achat : plat le moins cher (1000) offert, across les deux restaurants
        Purchase second = customer.makePurchase(List.of(
            new Order(restaurant, List.of(meal)),   // 1000 offert → 0
            new Order(other, List.of(otherMeal)))); // 1500

        assertThat(second.getPrice()).isEqualTo(1500);
    }
}
