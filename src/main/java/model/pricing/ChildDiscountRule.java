package model.pricing;

import model.user.Customer;
import model.restaurant.RestaurantOrder;

public class ChildDiscountRule implements DiscountRule
{
    @Override
    public boolean applies(Customer customer, RestaurantOrder restaurantOrder)
    {
        return customer.getType() == Customer.Type.CHILD;
    }

    @Override
    public double discount()
    {
        return 0.50;
    }
}
