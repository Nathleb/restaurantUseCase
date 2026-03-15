package model.user;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import model.Entity;
import model.restaurant.Meal;
import model.restaurant.Restaurant;
import static java.lang.String.format;
import static java.time.LocalDate.now;

public class Order implements Entity
{
    private static final double CHILD_DISCOUNT               = 0.50;
    private static final double STUDENT_DISCOUNT             = 0.25;
    private static final double RESTAURANT_LOYALTY_DISCOUNT  = 0.10;
    private static final double PLATFORM_LOYALTY_DISCOUNT    = 0.15;
    private static final int    RESTAURANT_LOYALTY_THRESHOLD = 5;
    private static final int    PLATFORM_LOYALTY_THRESHOLD   = 10;
    private static final int    PAST_WEEK_DAYS               = 7;
    private static final int    MINIMUM_MEALS_FOR_FREE       = 2;

    @Getter
    private final LocalDate date;

    @Getter
    private final Restaurant restaurant;

    @Getter
    private final Customer customer;

    @Getter
    private final List<Meal> meals;

    Order(Restaurant restaurant, Customer customer, List<Meal> meals)
    {
        Objects.requireNonNull(restaurant, "restaurant must not be null");
        Objects.requireNonNull(customer,   "customer must not be null");
        Objects.requireNonNull(meals,      "meals must not be null");
        this.date = now();
        this.restaurant = restaurant;
        this.customer = customer;
        this.meals = List.copyOf(meals);
    }

    // Ce n'est pas un nom, implem bizarre
    public String getName()
    {
        return format("From %s - in %s", customer.getName(), restaurant.getName());
    }

    public int getPrice()
    {
        int rawTotal = meals.stream().mapToInt(Meal::getPrice).sum() - cheapestMealPriceIfFree();
        return (int) Math.round(rawTotal * (1 - bestDiscount()));
    }

    private double bestDiscount()
    {
        if (customer.getType() == Customer.Type.CHILD)   return CHILD_DISCOUNT;
        if (customer.getType() == Customer.Type.STUDENT) return STUDENT_DISCOUNT;

        long ordersAtRestaurant = customer.getOrders().stream()
            .filter(o -> o.getRestaurant().equals(restaurant))
            .count();
        double restaurantLoyalty = (ordersAtRestaurant % RESTAURANT_LOYALTY_THRESHOLD == 0 && ordersAtRestaurant > 0)
            ? RESTAURANT_LOYALTY_DISCOUNT : 0.0;

        long totalOrders = customer.getOrders().size();
        double platformLoyalty = (totalOrders % PLATFORM_LOYALTY_THRESHOLD == 0 && totalOrders > 0)
            ? PLATFORM_LOYALTY_DISCOUNT : 0.0;

        return Math.max(restaurantLoyalty, platformLoyalty);
    }

    private int cheapestMealPriceIfFree()
    {
        if (!hasOrderedInThePastWeek() || meals.size() < MINIMUM_MEALS_FOR_FREE)
            return 0;
        return meals.stream().mapToInt(Meal::getPrice).min().getAsInt();
    }

    private boolean hasOrderedInThePastWeek()
    {
        return customer.getOrders().stream()
            .anyMatch(o -> o != this && ChronoUnit.DAYS.between(o.getDate(), now()) <= PAST_WEEK_DAYS);
    }
}
