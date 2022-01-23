package smart.roadtooth.service;

import smart.base.ActionResult;
import smart.roadtooth.model.EntryORExitModel;

public interface RoadToothService {
    ActionResult status(EntryORExitModel model);
}
