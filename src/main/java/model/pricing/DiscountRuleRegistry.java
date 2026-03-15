package model.pricing;

import java.util.List;

public class DiscountRuleRegistry
{
    private DiscountRuleRegistry() {}

    public static List<DiscountRule> rules()
    {
        return List.of(
            new ChildDiscountRule(),
            new StudentDiscountRule(),
            new RestaurantLoyaltyRule(),
            new PlatformLoyaltyRule()
        );
    }
}
