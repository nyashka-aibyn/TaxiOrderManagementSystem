
import java.util.ArrayList;
import java.util.List;

// 1. Strategy pattern
interface FareStrategy {
    double calculateFare(double distance);
}

class StandardFare implements FareStrategy {
    public double calculateFare(double distance) {
        return 500 + distance * 100;
    }
}

class ComfortFare implements FareStrategy {
    public double calculateFare(double distance) {
        return 800 + distance * 150;
    }
}

// 2. Observer pattern
interface OrderObserver {
    void update(String message);
}

class Customer implements OrderObserver {
    private String name;

    public Customer(String name) {
        this.name = name;
    }

    public void update(String message) {
        System.out.println(name + " хабарлама алды: " + message);
    }
}

// 3. Inheritance and polymorphism
abstract class Taxi {
    protected String driverName;

    public Taxi(String driverName) {
        this.driverName = driverName;
    }

    public abstract String getTaxiType();
}

class EconomyTaxi extends Taxi {
    public EconomyTaxi(String driverName) {
        super(driverName);
    }

    public String getTaxiType() {
        return "Эконом такси";
    }
}

class ComfortTaxi extends Taxi {
    public ComfortTaxi(String driverName) {
        super(driverName);
    }

    public String getTaxiType() {
        return "Комфорт такси";
    }
}

// 4. Factory pattern
class TaxiFactory {
    public static Taxi createTaxi(String type, String driver) {
        if (type.equalsIgnoreCase("economy")) {
            return new EconomyTaxi(driver);
        } else if (type.equalsIgnoreCase("comfort")) {
            return new ComfortTaxi(driver);
        }

        throw new IllegalArgumentException("Такси түрі табылмады!");
    }
}

// 5. Singleton pattern
class AppSettings {
    private static AppSettings instance;

    private AppSettings() {
    }

    public static AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    public String getApplicationName() {
        return "Taxi Order Management System";
    }
}

// 6. Builder pattern
class TaxiOrder {
    private int orderId;
    private String customerName;
    private String from;
    private String destination;
    private double distance;
    private Taxi taxi;
    private FareStrategy fareStrategy;

    private List<OrderObserver> observers = new ArrayList<>();

    private String status = "Жаңа тапсырыс";

    private TaxiOrder(Builder builder) {
        this.orderId = builder.orderId;
        this.customerName = builder.customerName;
        this.from = builder.from;
        this.destination = builder.destination;
        this.distance = builder.distance;
        this.taxi = builder.taxi;
        this.fareStrategy = builder.fareStrategy;
    }

    public static class Builder {
        private int orderId;
        private String customerName;
        private String from;
        private String destination;
        private double distance;
        private Taxi taxi;
        private FareStrategy fareStrategy;

        public Builder(int orderId) {
            this.orderId = orderId;
        }

        public Builder customerName(String name) {
            this.customerName = name;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder distance(double distance) {
            this.distance = distance;
            return this;
        }

        public Builder taxi(Taxi taxi) {
            this.taxi = taxi;
            return this;
        }

        public Builder fareStrategy(FareStrategy strategy) {
            this.fareStrategy = strategy;
            return this;
        }

        public TaxiOrder build() {
            return new TaxiOrder(this);
        }
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public void changeStatus(String newStatus) {
        status = newStatus;

        System.out.println("Тапсырыс күйі: " + status);

        for (OrderObserver observer : observers) {
            observer.update("Сіздің тапсырысыңыз: " + status);
        }
    }

    public double getPrice() {
        return fareStrategy.calculateFare(distance);
    }

    public void showOrder() {
        System.out.println("\n===== ТАКСИ ТАПСЫРЫСЫ =====");
        System.out.println("Тапсырыс нөмірі: " + orderId);
        System.out.println("Клиент: " + customerName);
        System.out.println("Қайдан: " + from);
        System.out.println("Қайда: " + destination);
        System.out.println("Қашықтық: " + distance + " км");
        System.out.println("Такси: " + taxi.getTaxiType());
        System.out.println("Жүргізуші: " + taxi.driverName);
        System.out.println("Бағасы: " + getPrice() + " тг");
        System.out.println("Күйі: " + status);
    }
}

// 7. Second interface
interface Payment {
    void pay(double amount);
}

class CashPayment implements Payment {
    public void pay(double amount) {
        System.out.println("Қолма-қол төлем: " + amount + " тг");
    }
}

class CardPayment implements Payment {
    public void pay(double amount) {
        System.out.println("Картамен төлем: " + amount + " тг");
    }
}

// 8. Repository interface
interface OrderRepository {
    void save(TaxiOrder order);
}

class InMemoryOrderRepository implements OrderRepository {
    public void save(TaxiOrder order) {
        System.out.println("Тапсырыс жүйеге сақталды!");
    }
}

// 9. Order service
class OrderService {
    private OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public void createOrder(TaxiOrder order) {
        repository.save(order);
        order.changeStatus("Жүргізуші ізделуде");
        order.changeStatus("Жүргізуші табылды");
        order.changeStatus("Жолда");
        order.changeStatus("Аяқталды");
    }
}

// 10. Main class
public class Main {

    public static void main(String[] args) {

        AppSettings settings = AppSettings.getInstance();

        System.out.println("================================");
        System.out.println(settings.getApplicationName());
        System.out.println("================================");

        Taxi taxi = TaxiFactory.createTaxi(
            "comfort", "Айдос"
        );

        Customer customer = new Customer("Айбын");

        TaxiOrder order = new TaxiOrder.Builder(101)
            .customerName("Айбын")
            .from("Алматы, Абая көшесі")
            .destination("Алматы, Достық көшесі")
            .distance(10)
            .taxi(taxi)
            .fareStrategy(new ComfortFare())
            .build();

        order.addObserver(customer);

        order.showOrder();

        OrderRepository repository =
            new InMemoryOrderRepository();

        OrderService service = new OrderService(repository);

        System.out.println("\n===== ТАПСЫРЫСТЫ ОРЫНДАУ =====");

        service.createOrder(order);

        Payment payment = new CardPayment();
        payment.pay(order.getPrice());

        System.out.println("\nБарлық операция аяқталды!");
    }
}
