package ma.jway.rms.controllers;


import ma.jway.rms.dto.models.Area;
import ma.jway.rms.dto.models.Floor;
import ma.jway.rms.dto.requests.AreaRequest;
import ma.jway.rms.repositories.AreaRepository;
import ma.jway.rms.repositories.FloorRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/areas")
public class AreasController {
    private final AreaRepository areaRepository;
    private final FloorRepository floorRepository;

    public AreasController(
            AreaRepository areaRepository,
            FloorRepository floorRepository) {
        this.areaRepository = areaRepository;
        this.floorRepository = floorRepository;
    }

    @PostMapping("/create")
    public Long createFloor(@RequestBody AreaRequest areaRequest) {
        Floor floor = floorRepository.findById(areaRequest.floor()).orElse(null);
        Area area = new Area();
        area.setFloor(floor);
        area.setName(areaRequest.name());
        areaRepository.save(area);
        return area.getId();
    }

}
