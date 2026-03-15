package model.pricing;

import model.user.Customer;
import model.restaurant.Order;

public class PlatformLoyaltyRule implements DiscountRule
{
    private static final int THRESHOLD = 10;

    @Override
    public boolean applies(Customer customer, Order order)
    {
        long count = customer.getPurchases().size();
        return count % THRESHOLD == 0 && count > 0;
    }

    @Override
    public double discount()
    {
        return 0.15;
    }
}
