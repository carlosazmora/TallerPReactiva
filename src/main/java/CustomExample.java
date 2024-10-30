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

        //Vamos a revisar el número mínimo de pedidos
        //Podemos catalogar aquellos cuya cantidad sea mayor a 5 Y de un producto específico


        //Determina una lista de órdenes
        List<Order> orders = Arrays.asList(
                new Order("Producto A", 5, 50.0),
                new Order("Producto B", 1, 30.0),
                new Order("Producto D", 1, 50.0),
                new Order("Producto C", 3, 20.0)
        );

        //Define variables delimitadoras
        int cantidadMinima = 5;
        String producto = "Producto A";

        // Procesar flujo de pedidos
        Flux<Order> ordersFlux = Flux.fromIterable(orders)
                .flatMap(order -> {
                    // Aplicar condición con flatMap
                    if (order.getQuantity() > cantidadMinima) {
                        return Flux.just(order);
                    } else {
                        return Flux.empty();
                    }
                });

        // Validar si el flujo está vacío
        Flux<String> result = ordersFlux
                .map(Order::toString)
                .mergeWith(Flux.just("No hay órdenes de cantidad mayor a " + cantidadMinima + " para " + producto + "."))
                .zipWith(Flux.range(1, orders.size()), (order, index) -> index <= orders.size() - 1 ? order : "Ninguna orden cumple los criterios.")
                .distinct();

        result.subscribe(System.out::println);

        //Esto se ejecuta cumpliendo las condiciones o no.
        //Es un reporte de ventas para el producto indicado
        Observable.fromIterable(orders)
                .filter(order -> producto.equals(order.getProduct())) // Filtrar por Producto delimitador
                .map(order -> order.getQuantity() * order.getPrice()) // Calcular el total
                .reduce(Double::sum) // Calcular el total de ventas
                .subscribe(total -> System.out.println("Ventas totales para " + producto + ": $" + total));
    }
}