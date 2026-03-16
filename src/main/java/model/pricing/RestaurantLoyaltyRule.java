package model.pricing;

import model.user.Customer;
import model.restaurant.RestaurantOrder;

public class RestaurantLoyaltyRule implements DiscountRule
{
    private static final int THRESHOLD = 5;

    @Override
    public boolean applies(Customer customer, RestaurantOrder restaurantOrder)
    {
        long count = customer.getOrders().stream()
            .filter(p -> p.getRestaurantOrders().stream()
                .anyMatch(o -> o.getRestaurant().equals(restaurantOrder.getRestaurant())))
            .count();
        return count % THRESHOLD == 0 && count > 0;
    }

    @Override
    public double discount()
    {
        return 0.10;
    }
}
