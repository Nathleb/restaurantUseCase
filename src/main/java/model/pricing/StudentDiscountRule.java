package model.pricing;

import model.user.Customer;
import model.restaurant.RestaurantOrder;

public class StudentDiscountRule implements DiscountRule
{
    @Override
    public boolean applies(Customer customer, RestaurantOrder restaurantOrder)
    {
        return customer.getType() == Customer.Type.STUDENT;
    }

    @Override
    public double discount()
    {
        return 0.25;
    }
}
