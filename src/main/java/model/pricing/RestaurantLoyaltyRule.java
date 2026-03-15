package model.pricing;

import model.user.Customer;
import model.restaurant.Order;

public class RestaurantLoyaltyRule implements DiscountRule
{
    private static final int THRESHOLD = 5;

    @Override
    public boolean applies(Customer customer, Order order)
    {
        long count = customer.getPurchases().stream()
            .filter(p -> p.getOrders().stream()
                .anyMatch(o -> o.getRestaurant().equals(order.getRestaurant())))
            .count();
        return count % THRESHOLD == 0 && count > 0;
    }

    @Override
    public double discount()
    {
        return 0.10;
    }
}
