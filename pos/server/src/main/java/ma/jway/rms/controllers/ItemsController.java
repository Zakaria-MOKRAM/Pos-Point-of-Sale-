package ma.jway.rms.controllers;

import ma.jway.rms.dto.responses.ItemResponse;
import ma.jway.rms.services.ItemsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(value = "/items")
public class ItemsController {
    private final ItemsService itemsService;

    public ItemsController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    @GetMapping("")
    public List<ItemResponse> getItems() throws IOException, SQLException {
        return itemsService.fetchSalableItems();
    }
}
