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
    private Meal       cheapMeal;
    private Meal       expensiveMeal;
    private Meal       otherMeal;

    @BeforeEach
    void setUp()
    {
        restaurant = new Restaurant("Le Ticino");
        restaurant.addMeal("Pizza",    "Recipe", 1000);
        restaurant.addMeal("Tiramisu", "Recipe", 2000);
        cheapMeal     = restaurant.getMealByName("Pizza");
        expensiveMeal = restaurant.getMealByName("Tiramisu");

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
    void makeOrder_emptyMeals_throws()
    {
        Customer customer = new Customer("A", "B", OTHER);

        assertThatThrownBy(() -> customer.makeOrder(restaurant, List.of()))
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
    void order_emptyMeals_throws()
    {
        assertThatThrownBy(() -> new Order(restaurant, List.of()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // --- Réductions profil ---

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

    // --- Fidélité restaurant ---

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

    // --- Fidélité plateforme ---

    @Test
    void getPrice_10thOrderOnPlatform_applies15PercentDiscount()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 9; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase tenth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(tenth.getPrice()).isEqualTo(850);
    }

    // --- Concurrence des réductions (meilleure gagne) ---

    @Test
    void getPrice_platformLoyaltyBeatsRestaurantLoyalty()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 5; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        for (int i = 0; i < 4; i++)
            customer.makeOrder(other, List.of(otherMeal));
        Purchase tenth = customer.makeOrder(other, List.of(otherMeal));

        assertThat(tenth.getPrice()).isEqualTo(1275); // 15% > 10%
    }

    @Test
    void getPrice_childOn5thRestaurantOrder_profileDiscountWins()
    {
        Customer customer = new Customer("A", "B", CHILD);
        for (int i = 0; i < 4; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase fifth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(fifth.getPrice()).isEqualTo(500); // 50% > 10%
    }

    @Test
    void getPrice_childOn10thPlatformOrder_profileDiscountWins()
    {
        Customer customer = new Customer("A", "B", CHILD);
        for (int i = 0; i < 9; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase tenth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(tenth.getPrice()).isEqualTo(500); // 50% > 15%
    }

    @Test
    void getPrice_studentOn5thRestaurantOrder_profileDiscountWins()
    {
        Customer customer = new Customer("A", "B", STUDENT);
        for (int i = 0; i < 4; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase fifth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(fifth.getPrice()).isEqualTo(750); // 25% > 10%
    }

    @Test
    void getPrice_studentOn10thPlatformOrder_profileDiscountWins()
    {
        Customer customer = new Customer("A", "B", STUDENT);
        for (int i = 0; i < 9; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase tenth = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(tenth.getPrice()).isEqualTo(750); // 25% > 15%
    }

    // --- Plat offert ---

    @Test
    void getPrice_firstOrderEver_noFreeMeal()
    {
        Customer customer = new Customer("A", "B", OTHER);
        Purchase purchase = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(purchase.getPrice()).isEqualTo(3000);
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
    void getPrice_secondOrderWithinAWeek_freeMealCombinedWithDiscount()
    {
        Customer customer = new Customer("A", "B", STUDENT);
        customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));
        Purchase second = customer.makeOrder(restaurant, List.of(cheapMeal, expensiveMeal));

        assertThat(second.getPrice()).isEqualTo(1500);
    }

    @Test
    void getPrice_secondOrderWithinAWeek_onlyOneMeal_noFreeMeal()
    {
        Customer customer = new Customer("A", "B", OTHER);
        customer.makeOrder(restaurant, List.of(cheapMeal));
        Purchase second = customer.makeOrder(restaurant, List.of(cheapMeal));

        assertThat(second.getPrice()).isEqualTo(1000);
    }

    // --- Multi-restaurants ---

    @Test
    void getPrice_multiRestaurant_sumsBothOrders()
    {
        Customer customer = new Customer("A", "B", OTHER);
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(cheapMeal)),
            new Order(other, List.of(otherMeal))));

        assertThat(purchase.getPrice()).isEqualTo(2500);
    }

    @Test
    void getPrice_multiRestaurant_childDiscountAppliesToAllOrders()
    {
        Customer customer = new Customer("A", "B", CHILD);
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(cheapMeal)),   // 1000 * 0.50 = 500
            new Order(other, List.of(otherMeal))));      // 1500 * 0.50 = 750

        assertThat(purchase.getPrice()).isEqualTo(1250);
    }

    @Test
    void getPrice_multiRestaurant_restaurantLoyaltyAppliesOnlyToItsOrder()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 4; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));

        // 2 meals chez restaurant pour que le plat offert reste dans cet order
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(cheapMeal, cheapMeal)), // (2000-1000) * 0.90 = 900
            new Order(other, List.of(otherMeal))));               // 1500 * 1.00 = 1500

        assertThat(purchase.getPrice()).isEqualTo(2400);
    }

    @Test
    void getPrice_multiRestaurant_platformLoyaltyAppliesToAllOrders()
    {
        Customer customer = new Customer("A", "B", OTHER);
        for (int i = 0; i < 9; i++)
            customer.makeOrder(restaurant, List.of(cheapMeal));

        // 2 meals chez restaurant pour que le plat offert reste dans cet order
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(cheapMeal, cheapMeal)), // (2000-1000) * 0.85 = 850
            new Order(other, List.of(otherMeal))));               // 1500 * 0.85 = 1275

        assertThat(purchase.getPrice()).isEqualTo(2125);
    }

    @Test
    void getPrice_multiRestaurant_cheapestMealFreeFromOtherRestaurant()
    {
        Restaurant cheap = new Restaurant("Le snack");
        cheap.addMeal("Sandwich", "Recipe", 500);
        Meal sandwich = cheap.getMealByName("Sandwich");

        Customer customer = new Customer("A", "B", OTHER);
        customer.makeOrder(restaurant, List.of(cheapMeal));

        // sandwich (500) est le moins cher, dans le 2ème restaurant
        Purchase purchase = customer.makePurchase(List.of(
            new Order(restaurant, List.of(expensiveMeal)), // 2000 plein prix
            new Order(cheap, List.of(sandwich))));         // 500 offert

        assertThat(purchase.getPrice()).isEqualTo(2000);
    }

    @Test
    void getPrice_multiRestaurant_cheapestMealFreeAcrossRestaurants()
    {
        Customer customer = new Customer("A", "B", OTHER);
        customer.makePurchase(List.of(
            new Order(restaurant, List.of(cheapMeal)),
            new Order(other, List.of(otherMeal))));

        // cheapMeal (1000) offert, dans le 1er restaurant
        Purchase second = customer.makePurchase(List.of(
            new Order(restaurant, List.of(cheapMeal)),  // 1000 offert → 0
            new Order(other, List.of(otherMeal))));     // 1500

        assertThat(second.getPrice()).isEqualTo(1500);
    }
}
