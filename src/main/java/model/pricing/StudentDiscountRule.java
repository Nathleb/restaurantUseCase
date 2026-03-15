package model.pricing;

import model.user.Customer;
import model.user.Order;

public class StudentDiscountRule implements DiscountRule
{
    @Override
    public boolean applies(Customer customer, Order order)
    {
        return customer.getType() == Customer.Type.STUDENT;
    }

    @Override
    public double discount()
    {
        return 0.25;
    }
}
