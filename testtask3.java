import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoffeeMachine {
    public static void main(String[] args) {
        MoneyMachine moneyMachine = new MoneyMachine();
        CoffeeMaker coffeeMaker = new CoffeeMaker();
        Menu menu = new Menu();
        boolean isOn = true;
        Scanner scanner = new Scanner(System.in);

        coffeeMaker.report();
        moneyMachine.report();

        while (isOn) {
            String options = menu.getItems();
            System.out.print("Что вы хотите заказать? (" + options + "):  ");
            String userChoice = scanner.nextLine().toLowerCase();

            if (userChoice.equals("отчет")) {
                coffeeMaker.report();
                moneyMachine.report();
            } else if (userChoice.equals("выкл.")) {
                System.out.println("Автомвт не работает.");
                isOn = false;
            } else {
                try {
                    MenuItem menuItem = menu.findDrink(userChoice);
                    if (menuItem != null) {
                        Drink drink = new Drink(menuItem.name,   menuItem.ingredients.get("вода"),   menuItem.ingredients.get("молоко"),   menuItem.ingredients.get("кофе"),   menuItem.cost);
                        boolean isEnoughIngredients = coffeeMaker.isResourceSufficient(drink);
                        if (isEnoughIngredients) {
                            boolean isPaymentSuccessful = moneyMachine.makePayment(drink.getCost());
                            if (isPaymentSuccessful) {
                                coffeeMaker.makeCoffee(drink);
                            }
                        }
                    } else {
                        System.out.println("Извините, этого кофе нет.");
                    }
                } catch (NullPointerException e) {
                    System.out.println("Извините, этого кофе нет.");
                }
            }
            System.out.println();
        }
        scanner.close();
    }
}

class Drink {
    String name;
    double cost;
    Map<String,   Integer> ingredients;

    public Drink(String name,   int water,   int milk,   int coffee,   double cost) {
        this.name = name;
        this.cost = cost;
        this.ingredients = new HashMap<>();
        this.ingredients.put("вода",   water);
        this.ingredients.put("молоко",   milk);
        this.ingredients.put("кофе",   coffee);
    }

    public double getCost() {
        return cost;
    }
}

class Order {
    String name;
    Map<String,   Integer> ingredients;

    public Order(String name,   Map<String,   Integer> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }
}

class MenuItem {
    String name;
    double cost;
    Map<String,  Integer> ingredients;

    public MenuItem(String name,  int water,  int milk,  int coffee,  int cost) {
        this.name = name;
        this.cost = cost;
        this.ingredients = new HashMap<>();
        this.ingredients.put("вода",  water);
        this.ingredients.put("молоко",  milk);
        this.ingredients.put("кофе",  coffee);
    }
}

class Menu {
    List<MenuItem> menu;

    public Menu() {
        menu = new ArrayList<>();
        menu.add(new MenuItem("американо",  254,  0,  18,  2500));
        menu.add(new MenuItem("эспрессо",  50,  0,  18,  1500));
        menu.add(new MenuItem("капучино",  250,  50,  24,  3000));
    }

    public String getItems() {
        StringBuilder options = new StringBuilder();
        for (MenuItem item :  menu) {
            options.append(item.name).append("/");
        }
        return options.toString();
    }

    public MenuItem findDrink(String orderName) {
        for (MenuItem item :  menu) {
            if (item.name.equals(orderName)) {
                return item;
            }
        }
        return null;
    }
}

class CoffeeMaker {
    private Map<String,  Integer> resources;

    public CoffeeMaker() {
        resources = new HashMap<>();
        resources.put("вода",  300);
        resources.put("молоко",  200);
        resources.put("кофе",  100);
    }

    public void report() {
        System.out.println("Вода:  " + resources.get("вода") + "мл");
        System.out.println("Молоко:  " + resources.get("молоко") + "мл");
        System.out.println("Кофе:  " + resources.get("кофе") + "г");
    }

    public boolean isResourceSufficient(Drink drink) {
        boolean canMake = true;
        for (String item :  drink.ingredients.keySet()) {
            if (drink.ingredients.get(item) > resources.get(item)) {
                System.out.println("Извините, "+ item + " не достаточно.");
                canMake = false;
            }
        }
        return canMake;
    }

    public void makeCoffee(Drink drink) {
        for (String item :  drink.ingredients.keySet()) {
            resources.put(item,  resources.get(item) - drink.ingredients.get(item));
        }
        System.out.println("Здесь ваше " + drink.name + " ☕️. Пейте на здоровье!");
    }
}

class MoneyMachine {

    private static final String CURRENCY = "KZT";

    private static final Map<String,  Integer> COIN_VALUES = new HashMap<String,  Integer>() {{
        put("купюр в 5 тысяч тенге", 5000);
        put("купюр в 1 тысяча тенге", 1000);
        put("купюр в 500 тенге ", 500);
        put("монет в 100 тенге", 100);
    }};

    private int profit;
    private double moneyReceived;

    public MoneyMachine() {
        this.profit = 0;
        this.moneyReceived = 0;
    }

    public void report() {
        System.out.println("Итого: " + profit + CURRENCY);
    }

    public double processCoins() {
        System.out.println("Пожалуйста внесите купюру или монету.");
        Scanner scanner = new Scanner(System.in);
        for (String coin :  COIN_VALUES.keySet()) {
            System.out.print("Как много " + coin + "?: ");
            int count = scanner.nextInt();
            moneyReceived += count * COIN_VALUES.get(coin);
        }
        return moneyReceived;
    }

    public boolean makePayment(double cost) {
        processCoins();
        if (moneyReceived >= cost) {
            double change = moneyReceived - cost;
            System.out.println("Здесь ваша сдача " + change + CURRENCY);
            profit += cost;
            moneyReceived = 0;
            return true;
        } else {
            System.out.println("Извините, средств недостаточно. Деньги возвращены.");
            moneyReceived = 0;
            return false;
        }
    }
}
