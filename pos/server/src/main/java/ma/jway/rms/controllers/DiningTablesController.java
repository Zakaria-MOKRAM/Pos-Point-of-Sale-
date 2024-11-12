package ma.jway.rms.controllers;

import ma.jway.rms.dto.enums.DiningTableStatus;
import ma.jway.rms.dto.models.Area;
import ma.jway.rms.dto.models.DiningTable;
import ma.jway.rms.dto.models.DiningTableGeometry;
import ma.jway.rms.dto.requests.DiningTableRequest;
import ma.jway.rms.dto.requests.UpdateDiningTableRequest;
import ma.jway.rms.repositories.AreaRepository;
import ma.jway.rms.repositories.DiningTableGeometryRepository;
import ma.jway.rms.repositories.DiningTableRepository;
import ma.jway.rms.services.OrderService;

import java.util.HashMap;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/tables")
@RequiredArgsConstructor
public class DiningTablesController {
    private final DiningTableRepository diningTableRepository;
    private final DiningTableGeometryRepository diningTableGeometryRepository;
    private final AreaRepository areaRepository;
    private final OrderService orderService;

    @PostMapping("/create")
    public Long createTable(@RequestBody DiningTableRequest diningTableRequest) {
        Area area = areaRepository.findById(diningTableRequest.area()).orElse(null);

        DiningTable diningTable = new DiningTable();
        diningTable.setArea(area);
        diningTable.setChairs(diningTableRequest.chairs());
        diningTable.setNumber(diningTableRequest.number());
        diningTableRepository.save(diningTable);

        DiningTableGeometry diningTableGeometry = new DiningTableGeometry();
        diningTableGeometry.setDiningTable(diningTable);
        diningTableGeometry.setY(diningTableRequest.geometry().y());
        diningTableGeometry.setX(diningTableRequest.geometry().x());
        diningTableGeometry.setWidth(diningTableRequest.geometry().width());
        diningTableGeometry.setHeight(diningTableRequest.geometry().height());
        diningTableGeometry.setShape(diningTableRequest.geometry().shape());
        diningTableGeometryRepository.save(diningTableGeometry);

        return diningTable.getId();
    }

    @PostMapping("/update")
    public void updateTable(@RequestBody UpdateDiningTableRequest updateDiningTableRequest) {
        DiningTable diningTable = diningTableRepository.findById(updateDiningTableRequest.id()).orElse(null);
        if (diningTable != null) {
            DiningTableGeometry diningTableGeometry = diningTableGeometryRepository.findByDiningTable(diningTable);

            diningTable.setNumber(updateDiningTableRequest.number());
            diningTable.setChairs(updateDiningTableRequest.chairs());
            diningTableRepository.save(diningTable);

            diningTableGeometry.setX(updateDiningTableRequest.x());
            diningTableGeometry.setY(updateDiningTableRequest.y());
            diningTableGeometryRepository.save(diningTableGeometry);
        }
    }

    @PostMapping("/update/status")
    public void updateTableStatus(@RequestParam String status, @RequestParam Long id) {
        DiningTable diningTable = diningTableRepository.findById(id).orElse(null);
        if (diningTable != null) {
            diningTable.setStatus(DiningTableStatus.valueOf(status));
            diningTableRepository.save(diningTable);
        }
    }

    @PutMapping("/delete/{id}")
    public void deleteTable(@PathVariable Long id) {
        DiningTable diningTable = diningTableRepository.findById(id).orElse(null);
        if (diningTable != null) {
            diningTable.setDeleted(true);
            diningTableRepository.save(diningTable);
        }
    }

    @GetMapping("/order")
    public HashMap<String, Object> associatedOrder(@RequestParam Long id) {
        return orderService.getLatestOrderByDiningTable(id);
    }
}
