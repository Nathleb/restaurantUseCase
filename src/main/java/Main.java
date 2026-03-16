import java.util.List;

import model.restaurant.Restaurant;
import model.user.Customer;
import model.restaurant.RestaurantOrder;
import model.user.RestaurantOwner;
import static model.user.Customer.Type.OTHER;

public class Main
{
    public static void main(String[] args)
    {
        // *******************
        // *** RESTAURANTS ***
        // *******************

        Restaurant ticino = new Restaurant("Le Ticino");
        Restaurant etoile = new Restaurant("L'étoile");
        Restaurant texan  = new Restaurant("Le texan");

        // *************
        // *** USERS ***
        // *************

        RestaurantOwner robertDupont  = new RestaurantOwner("Robert",    "Dupont",  ticino);
        RestaurantOwner magaliNoel    = new RestaurantOwner("Magali",    "Noel",    etoile);
        RestaurantOwner nicolasBenoit = new RestaurantOwner("Nicolas",   "Benoit",  texan);

        Customer catherine  = new Customer("Catherine",  "Zwahlen", OTHER);
        Customer clementine = new Customer("Clementine", "Delerce",  OTHER);

        // *************
        // *** MEALS ***
        // *************

        robertDupont.addMeal("Pizza tonno",     "Pâte, sauce tomate, thon",              2050);
        robertDupont.addMeal("Pasta bolognese", "Pâtes, viande hachée, sauce tomate",    1830);
        robertDupont.addMeal("Tiramisu",        "Mascarpone, café, biscuits",             1280);

        magaliNoel.addMeal("Cassoulet",    "Haricots blancs, saucisse, confit de canard", 2260);
        magaliNoel.addMeal("Risotto",      "Riz arborio, parmesan, bouillon",             1915);
        magaliNoel.addMeal("Banana split", "Banane, glace vanille, chantilly",            1490);

        nicolasBenoit.addMeal("Burger vege", "Pain brioche, steak végétal, cheddar", 2110);
        nicolasBenoit.addMeal("Fajitas",     "Tortilla, poulet, poivrons, épices",   2400);

        // **************
        // *** ORDERS ***
        // **************

        // Commande mono-restaurant
        catherine.makeOrder(ticino, List.of(
            ticino.getMealByName("Pizza tonno"),
            ticino.getMealByName("Tiramisu")));

        // Commande multi-restaurants
        catherine.makeOrder(List.of(
            new RestaurantOrder(ticino, List.of(ticino.getMealByName("Pasta bolognese"))),
            new RestaurantOrder(etoile, List.of(etoile.getMealByName("Risotto"), etoile.getMealByName("Banana split")))));

        clementine.makeOrder(etoile, List.of(
            etoile.getMealByName("Risotto"),
            etoile.getMealByName("Banana split")));

        System.out.println("done");
    }
}
