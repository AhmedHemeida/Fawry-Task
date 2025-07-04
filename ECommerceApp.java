import java.util.*;

class Product {
    String name;
    double price;
    int quantity;
    boolean hasExpiry;
    Date expiryDate;
    boolean needsShipping;
    double weight;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}

class CartItem {
    Product product;
    int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return product.price * quantity;
    }
}

class Cart {
    List<CartItem> items = new ArrayList<>();

    public void add(Product product, int quantity) {
        if (quantity > product.quantity) {
            System.out.println("The available quantity is " + product.quantity);
            throw new IllegalArgumentException(" Not enough quantity");
        }
        items.add(new CartItem(product, quantity));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}

class Customer {
    String name;
    double balance;

    public Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }
}

class ShippingItem {
    String name;
    double weight;

    public ShippingItem(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }
}

class ShippingService {
    public static void ship(List<ShippingItem> items) {
        double totalWeight = 0;
        System.out.println("** Shipment notice **");
        for (ShippingItem item : items) {
            System.out.println("1x " + item.name);
            totalWeight += item.weight;
        }
        System.out.println("Total package weight " + totalWeight + "kg");
    }
}

public class ECommerceApp {
    public static void checkout(Customer customer, Cart cart) {
        if (cart.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double subtotal = 0;
        double shipping = 0;
        List<ShippingItem> shippableItems = new ArrayList<>();
        Date now = new Date();

        for (CartItem item : cart.items) {
            Product p = item.product;
            if (p.quantity < item.quantity) {
                throw new RuntimeException(p.name + " is out of stock");
            }
            if (p.hasExpiry && p.expiryDate.before(now)) {
                throw new RuntimeException(p.name + " is expired");
            }
            subtotal += item.getTotalPrice();
            if (p.needsShipping) {
                for (int i = 0; i < item.quantity; i++) {
                    shippableItems.add(new ShippingItem(p.name, p.weight));
                    shipping += p.weight * 10;
                }
            }
        }

        double total = subtotal + shipping;

        if (customer.balance < total) {
            throw new RuntimeException("Insufficient balance");
        }

        customer.balance -= total;

        if (!shippableItems.isEmpty()) {
            ShippingService.ship(shippableItems);
        }

        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.items) {
            System.out.println(item.quantity + "x " + item.product.name + " " + item.getTotalPrice());
        }
        System.out.println("----------------------");
        System.out.println("Subtotal " + subtotal);
        System.out.println("Shipping " + shipping);
        System.out.println("Amount " + total);
        System.out.println("Remaining balance " + customer.balance);
    }

    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 5);

        Product cheese = new Product("Cheese 200g", 100, 10);
        cheese.hasExpiry = true;
        cheese.expiryDate = cal.getTime();
        cheese.needsShipping = true;
        cheese.weight = 0.2;

        Product biscuits = new Product("Biscuits 800g", 150, 5);
        biscuits.hasExpiry = true;
        biscuits.expiryDate = cal.getTime();
        biscuits.needsShipping = true;
        biscuits.weight = 0.8;

        Product scratchCard = new Product("Scratch Card", 50, 20);
        scratchCard.needsShipping = false;

        Product tv = new Product("TV", 5000, 2);
        tv.hasExpiry = false;
        tv.needsShipping = true;
        tv.weight = 8.0;

        Customer customer = new Customer("Hemeida", 20000);
        Cart cart = new Cart();

        cart.add(tv, 3);
        checkout(customer, cart);
    }
}
