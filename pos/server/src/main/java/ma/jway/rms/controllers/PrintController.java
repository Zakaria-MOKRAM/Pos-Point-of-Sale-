package ma.jway.rms.controllers;

import ma.jway.rms.services.PrinterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/print")
public class PrintController {
    private final PrinterService printerService;

    public PrintController(PrinterService printerService) {
        this.printerService = printerService;
    }

    @PostMapping("/order")
    public boolean printOrder(@RequestParam Long id) {
        try {
            printerService.processOrder(id);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/receipt")
    public boolean printReceipt(@RequestParam Long id) {
        try {
            printerService.printCheckoutReceipt(id);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
