package model.pricing;

import model.user.Customer;
import model.restaurant.RestaurantOrder;

public interface DiscountRule
{
    boolean applies(Customer customer, RestaurantOrder order);
    double discount();
}
