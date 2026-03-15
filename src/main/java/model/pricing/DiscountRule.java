package model.pricing;

import model.user.Customer;
import model.restaurant.Order;

public interface DiscountRule
{
    boolean applies(Customer customer, Order order);
    double discount();
}
