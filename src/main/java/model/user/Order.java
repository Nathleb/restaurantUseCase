package model.user;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.Getter;
import model.pricing.DiscountRule;
import model.restaurant.Meal;
import model.restaurant.RestaurantOrder;

import static java.time.LocalDate.now;

public class Order
{
    private static final int PAST_WEEK_DAYS         = 6;
    private static final int MINIMUM_MEALS_FOR_FREE = 2;

    @Getter
    private final LocalDate date;

    @Getter
    private final Customer customer;

    private final List<RestaurantOrder>        restaurantOrders;
    private final List<DiscountRule> discountRules;
    private int                      price;

    Order(Customer customer, List<RestaurantOrder> restaurantOrders, List<DiscountRule> discountRules)
    {
        this.customer      = Objects.requireNonNull(customer,      "customer must not be null");
        this.discountRules = Objects.requireNonNull(discountRules, "discountRules must not be null");
        Objects.requireNonNull(restaurantOrders, "orders must not be null");
        this.restaurantOrders = List.copyOf(restaurantOrders);
        this.date   = now();
    }

    public List<RestaurantOrder> getRestaurantOrders()
    {
        return restaurantOrders;
    }

    public int getPrice()
    {
        return price;
    }

    void initPrice()
    {
        Optional<Meal> freeMeal = cheapestMealIfFree();
        this.price = restaurantOrders.stream()
            .mapToInt(order -> priceFor(order, freeMeal))
            .sum();
    }

    private int priceFor(RestaurantOrder restaurantOrder, Optional<Meal> freeMeal)
    {
        int free = freeMeal.filter(restaurantOrder.getMeals()::contains)
                           .map(Meal::getPrice)
                           .orElse(0);
        return (int) Math.round((restaurantOrder.getRawPrice() - free) * (1 - bestDiscountFor(restaurantOrder)));
    }

    private double bestDiscountFor(RestaurantOrder restaurantOrder)
    {
        return discountRules.stream()
            .filter(rule -> rule.applies(customer, restaurantOrder))
            .mapToDouble(DiscountRule::discount)
            .max()
            .orElse(0.0);
    }

    private Optional<Meal> cheapestMealIfFree()
    {
        int totalMeals = restaurantOrders.stream().mapToInt(o -> o.getMeals().size()).sum();
        if (!hasOrderedInThePastWeek() || totalMeals < MINIMUM_MEALS_FOR_FREE)
            return Optional.empty();
        return restaurantOrders.stream()
            .flatMap(o -> o.getMeals().stream())
            .min(Comparator.comparingInt(Meal::getPrice));
    }

    private boolean hasOrderedInThePastWeek()
    {
        return customer.getOrders().stream()
            .anyMatch(p -> p != this && ChronoUnit.DAYS.between(p.getDate(), now()) <= PAST_WEEK_DAYS);
    }
}
