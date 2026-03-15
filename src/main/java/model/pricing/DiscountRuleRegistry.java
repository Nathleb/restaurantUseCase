package model.pricing;

import java.util.List;

public class DiscountRuleRegistry
{
    private DiscountRuleRegistry() {}

    private static final List<DiscountRule> RULES = List.of(
        new ChildDiscountRule(),
        new StudentDiscountRule(),
        new RestaurantLoyaltyRule(),
        new PlatformLoyaltyRule()
    );

    public static List<DiscountRule> rules()
    {
        return RULES;
    }
}
