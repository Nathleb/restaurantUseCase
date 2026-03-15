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

import static java.time.LocalDate.now;

public class Purchase
{
    private static final int PAST_WEEK_DAYS         = 7;
    private static final int MINIMUM_MEALS_FOR_FREE = 2;

    @Getter
    private final LocalDate date;

    @Getter
    private final Customer customer;

    private final List<Order>        orders;
    private final List<DiscountRule> discountRules;

    Purchase(Customer customer, List<Order> orders, List<DiscountRule> discountRules)
    {
        this.customer      = Objects.requireNonNull(customer,      "customer must not be null");
        this.discountRules = Objects.requireNonNull(discountRules, "discountRules must not be null");
        Objects.requireNonNull(orders, "orders must not be null");
        this.orders = List.copyOf(orders);
        this.date   = now();
    }

    public List<Order> getOrders()
    {
        return orders;
    }

    public int getPrice()
    {
        Optional<Meal> freeMeal = cheapestMealIfFree();
        return orders.stream()
            .mapToInt(order -> priceFor(order, freeMeal))
            .sum();
    }

    private int priceFor(Order order, Optional<Meal> freeMeal)
    {
        int free = freeMeal.filter(order.getMeals()::contains)
                           .map(Meal::getPrice)
                           .orElse(0);
        return (int) Math.round((order.getRawPrice() - free) * (1 - bestDiscountFor(order)));
    }

    private double bestDiscountFor(Order order)
    {
        return discountRules.stream()
            .filter(rule -> rule.applies(customer, order))
            .mapToDouble(DiscountRule::discount)
            .max()
            .orElse(0.0);
    }

    private Optional<Meal> cheapestMealIfFree()
    {
        int totalMeals = orders.stream().mapToInt(o -> o.getMeals().size()).sum();
        if (!hasOrderedInThePastWeek() || totalMeals < MINIMUM_MEALS_FOR_FREE)
            return Optional.empty();
        return orders.stream()
            .flatMap(o -> o.getMeals().stream())
            .min(Comparator.comparingInt(Meal::getPrice));
    }

    private boolean hasOrderedInThePastWeek()
    {
        return customer.getPurchases().stream()
            .anyMatch(p -> p != this && ChronoUnit.DAYS.between(p.getDate(), now()) <= PAST_WEEK_DAYS);
    }
}
