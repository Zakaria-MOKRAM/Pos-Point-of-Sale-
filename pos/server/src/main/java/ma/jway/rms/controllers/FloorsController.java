package ma.jway.rms.controllers;

import ma.jway.rms.dto.models.Area;
import ma.jway.rms.dto.models.DiningTable;
import ma.jway.rms.dto.models.DiningTableGeometry;
import ma.jway.rms.dto.models.Floor;
import ma.jway.rms.dto.models.User;
import ma.jway.rms.dto.models.WaiterAreas;
import ma.jway.rms.dto.requests.FloorRequest;
import ma.jway.rms.dto.responses.AreaResponse;
import ma.jway.rms.dto.responses.DiningTableGeometryResponse;
import ma.jway.rms.dto.responses.DiningTableResponse;
import ma.jway.rms.dto.responses.FloorResponse;
import ma.jway.rms.repositories.DiningTableGeometryRepository;
import ma.jway.rms.repositories.DiningTableRepository;
import ma.jway.rms.repositories.FloorRepository;
import ma.jway.rms.repositories.WaiterAreasRepository;
import ma.jway.rms.services.AreasAndFloorsService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/floors")
@RequiredArgsConstructor
public class FloorsController {
    private final FloorRepository floorRepository;
    private final DiningTableRepository diningTableRepository;
    private final DiningTableGeometryRepository diningTableGeometryRepository;
    private final WaiterAreasRepository waiterAreasRepository;
    private final AreasAndFloorsService areasAndFloorsService;

    @GetMapping("")
    public List<FloorResponse> getFloors(@AuthenticationPrincipal User user) {
        List<WaiterAreas> waiterArea = waiterAreasRepository.findByUser(user);
        List<Floor> floorsList = floorRepository.findAll();

        List<Floor> floors = areasAndFloorsService.getCommonFloors(floorsList, waiterArea);

        if (!floors.isEmpty()) {
            List<FloorResponse> floorResponseList = new ArrayList<>();
            for (Floor floor : floors) {
                List<AreaResponse> areaResponseList = new ArrayList<>();
                if (!floor.getAreas().isEmpty()) {
                    List<Area> areas = areasAndFloorsService.getCommonAreas(floor.getAreas(), waiterArea);
                    for (Area area : areas) {
                        List<DiningTable> diningTables = diningTableRepository.findByArea(area);
                        List<DiningTableResponse> diningTableResponseList = new ArrayList<>();
                        if (!diningTables.isEmpty()) {
                            for (DiningTable diningTable : diningTables) {
                                if (!diningTable.getDeleted()) {
                                    DiningTableGeometry diningTableGeometry = diningTableGeometryRepository
                                            .findByDiningTable(diningTable);
                                    DiningTableGeometryResponse diningTableGeometryResponse = new DiningTableGeometryResponse(
                                            diningTableGeometry.getX(),
                                            diningTableGeometry.getY(),
                                            diningTableGeometry.getWidth(),
                                            diningTableGeometry.getHeight(),
                                            diningTableGeometry.getShape());
                                    diningTableResponseList.add(new DiningTableResponse(
                                            diningTable.getId(),
                                            diningTable.getNumber(),
                                            diningTable.getChairs(),
                                            diningTable.getStatus(),
                                            diningTableGeometryResponse));
                                }
                            }
                        }
                        areaResponseList.add(new AreaResponse(
                                area.getId(), area.getName(), area.getDescription(), diningTableResponseList));
                    }
                }
                floorResponseList.add(new FloorResponse(
                        floor.getId(), floor.getName(), floor.getReference(),
                        floor.getDescription(), areaResponseList));
            }
            return floorResponseList;
        }
        return new ArrayList<>();
    }

    @PostMapping("/create")
    public Long createFloor(@RequestBody FloorRequest floorRequest) {
        Floor floor = new Floor();
        floor.setName(floorRequest.name());
        floor.setReference(floorRequest.name().replaceAll("\\s", "-").toLowerCase());
        floorRepository.save(floor);
        return floor.getId();
    }
}
