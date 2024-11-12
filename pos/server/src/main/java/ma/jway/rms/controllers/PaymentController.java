package ma.jway.rms.controllers;

import ma.jway.rms.dto.enums.DiningTableStatus;
import ma.jway.rms.dto.enums.OrderStatus;
import ma.jway.rms.dto.enums.OrderType;
import ma.jway.rms.dto.models.DiningTable;
import ma.jway.rms.dto.models.Order;
import ma.jway.rms.dto.models.Payment;
import ma.jway.rms.dto.requests.PaymentRequest;
import ma.jway.rms.repositories.DiningTableRepository;
import ma.jway.rms.repositories.OrderRepository;
import ma.jway.rms.repositories.PaymentRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/payments")
public class PaymentController {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final DiningTableRepository diningTableRepository;

    public PaymentController(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            DiningTableRepository diningTableRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.diningTableRepository = diningTableRepository;
    }

    @PostMapping("/order")
    public void printOrder(@RequestBody List<PaymentRequest> paymentRequestList) {
        List<Payment> payments = new ArrayList<>();
        for (PaymentRequest paymentRequest : paymentRequestList) {
            Order order = orderRepository.findById(paymentRequest.orderId()).orElse(null);
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            if (order.getOrderType() == OrderType.AT_THE_TABLE) {
                DiningTable diningTable = order.getDiningTable();
                diningTable.setStatus(DiningTableStatus.AVAILABLE);
                diningTableRepository.save(diningTable);
            }

            Payment payment = new Payment();
            payment.setPaymentType(paymentRequest.paymentType());
            payment.setAmount(paymentRequest.amount());
            payment.setOrder(order);

            payments.add(payment);
        }

        paymentRepository.saveAll(payments);
    }
}
