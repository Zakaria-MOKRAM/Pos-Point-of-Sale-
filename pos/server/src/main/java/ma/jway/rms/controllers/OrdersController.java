package ma.jway.rms.controllers;

import ma.jway.rms.dto.enums.DiningTableStatus;
import ma.jway.rms.dto.models.DiningTable;
import ma.jway.rms.dto.models.Order;
import ma.jway.rms.dto.requests.OrderRequest;
import ma.jway.rms.dto.responses.OrderResponse;
import ma.jway.rms.repositories.DiningTableRepository;
import ma.jway.rms.repositories.OrderRepository;
import ma.jway.rms.services.OrderService;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@RequestMapping(value = "/orders")
@AllArgsConstructor
public class OrdersController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final DiningTableRepository diningTableRepository;

    @GetMapping("")
    public List<OrderResponse> findOrders() {
        return orderService.getOrders();
    }

    @PostMapping("/create")
    public Long createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        return order.getId();
    }

    @PostMapping("/guests")
    public void updateGuests(@RequestParam Integer guests, @RequestParam Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setGuests(guests);
            orderRepository.save(order);
        }
    }

    @PostMapping("/update-table")
    public void updateDiningTable(@RequestParam Long table, @RequestParam Long order) {
        DiningTable diningTable = diningTableRepository.findById(table).orElse(null);
        Order fetchedOrder = orderRepository.findById(order).orElse(null);

        if (fetchedOrder != null && diningTable != null) {
            DiningTable prevDiningTable = fetchedOrder.getDiningTable();
            prevDiningTable.setStatus(DiningTableStatus.AVAILABLE);
            diningTableRepository.save(prevDiningTable);

            fetchedOrder.setDiningTable(diningTable);
            orderRepository.save(fetchedOrder);

            diningTable.setStatus(DiningTableStatus.RESERVED);
            diningTableRepository.save(diningTable);
        }
    }

}
