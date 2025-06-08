package integral.studios.hydro.service;

import integral.studios.hydro.model.gui.IGUI;
import integral.studios.hydro.model.gui.impl.MainGUI;
import integral.studios.hydro.model.gui.impl.check.CheckGUI;
import integral.studios.hydro.model.gui.impl.check.combat.CombatChecksGUI;
import integral.studios.hydro.model.gui.impl.check.combat.impl.*;
import integral.studios.hydro.model.gui.impl.check.misc.impl.BadPacketsGUI;
import integral.studios.hydro.model.gui.impl.check.misc.impl.PingSpoofGUI;
import integral.studios.hydro.model.gui.impl.check.misc.impl.PostGUI;
import integral.studios.hydro.model.gui.impl.check.misc.impl.TimerGUI;
import integral.studios.hydro.model.gui.impl.check.movement.impl.FlightGUI;
import integral.studios.hydro.model.gui.impl.check.movement.impl.GroundGUI;
import integral.studios.hydro.model.gui.impl.check.movement.impl.MotionGUI;
import integral.studios.hydro.model.gui.impl.check.movement.impl.SpeedGUI;
import integral.studios.hydro.model.gui.impl.check.combat.impl.*;
import integral.studios.hydro.model.gui.impl.check.misc.MiscChecksGUI;
import integral.studios.hydro.model.gui.impl.check.misc.impl.*;
import integral.studios.hydro.model.gui.impl.check.movement.MovementChecksGUI;
import integral.studios.hydro.model.gui.impl.check.movement.impl.*;
import integral.studios.hydro.model.gui.impl.misc.ManageGUI;

import java.util.Arrays;
import java.util.List;

/**
 * @author Salers
 * made on integral.studios.hydro.service
 */
public class GuiService {
    private final List<IGUI> guis;

    public GuiService() {
        this.guis = Arrays.asList(
                new MainGUI(),
                new ManageGUI(),
                new CheckGUI(),
                new CombatChecksGUI(),
                new AimAssistGUI(),
                new AutoClickerGUI(),
                new KillAuraGUI(),
                new VelocityGUI(),
                new ReachGUI(),
                new MovementChecksGUI(),
                new FlightGUI(),
                new GroundGUI(),
                new MotionGUI(),
                new SpeedGUI(),
                new MiscChecksGUI(),
                new BadPacketsGUI(),
                new PingSpoofGUI(),
                new PostGUI(),
                new TimerGUI()

        );
    }

    public <T extends IGUI> T getByClass(Class<T> clazz){
        return (T) guis.stream().filter(p -> p.getClass().equals(clazz)).findAny().orElse(null);
    }
}