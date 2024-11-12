package ma.jway.rms.services;

import ma.jway.rms.dto.models.Area;
import ma.jway.rms.dto.models.Floor;
import ma.jway.rms.dto.models.WaiterAreas;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

@Service
public class AreasAndFloorsService {

    @Transactional
    public List<Floor> getCommonFloors(List<Floor> floors, List<WaiterAreas> waiterAreas) {
        List<Floor> floorsInWaiterArea = new ArrayList<>();
        for (WaiterAreas wa : waiterAreas) {
            floorsInWaiterArea.add(wa.getFloor());
        }

        if (floorsInWaiterArea.isEmpty()) {
            return floors;
        }

        List<Floor> commonFloors = new ArrayList<>();
        for (Floor floor : floors) {
            if (floorsInWaiterArea.contains(floor)) {
                commonFloors.add(floor);
            }
        }
        return commonFloors;
    }

    @Transactional
    public List<Area> getCommonAreas(List<Area> areas, List<WaiterAreas> waiterAreas) {
        List<Area> areasInWaiterArea = new ArrayList<>();
        for (WaiterAreas wa : waiterAreas) {
            areasInWaiterArea.add(wa.getArea());
        }

        if (areasInWaiterArea.isEmpty()) {
            return areas;
        }

        List<Area> commonAreas = new ArrayList<>();
        for (Area area : areas) {
            if (areasInWaiterArea.contains(area)) {
                commonAreas.add(area);
            }
        }
        return commonAreas;
    }
}
