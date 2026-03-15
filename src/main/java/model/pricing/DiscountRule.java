package model.pricing;

import model.user.Customer;
import model.user.Order;

public interface DiscountRule
{
    boolean applies(Customer customer, Order order);
    double discount();
}
