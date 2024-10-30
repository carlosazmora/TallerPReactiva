import io.reactivex.Observable;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

class Order {
    private final String product;
    private final int quantity;
    private final double price;

    public Order(String product, int quantity, double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Producto = " + product +
                ", cantidad=" + quantity +
                ", precio= $" + price +
                '.';
    }
}

public class CustomExample {
    public static void main(String[] args) {

        //Define lista de productos
        List<Order> orders = Arrays.asList(
                new Order("Producto A", 5, 50.0),
                new Order("Producto B", 1, 30.0),
                new Order("Producto D", 1, 50.0),
                new Order("Producto C", 3, 20.0)
        );

        //Estos son los parámetros de validación
        int cantidadMinima = 5;
        String producto = "Producto A";

        // Procesar flujo de pedidos
        Flux<Order> ordersFlux = Flux.fromIterable(orders)
                .flatMap(order -> {
                    if (order.getQuantity() > cantidadMinima) {
                        return Flux.just(order);
                    } else {
                        return Flux.empty();
                    }
                });

        // Validar si el flujo está vacío
        ordersFlux
                .map(Order::toString)
                .zipWith(Flux.range(1, orders.size()), (order, index) -> order)
                .switchIfEmpty(Flux.just("Ninguna orden cumple los criterios."))
                .distinct()
                .subscribe(System.out::println);

        // Reporte de ventas para el producto indicado
        // Se despliega en cualquier caso
        Observable.fromIterable(orders)
                .filter(order -> producto.equals(order.getProduct()))
                .map(order -> order.getQuantity() * order.getPrice())
                .reduce(Double::sum)
                .subscribe(total -> System.out.println("Ventas totales para " + producto + ": $" + total));
    }
}