package integral.studios.hydro.model.emulator.handler;

import integral.studios.hydro.model.PlayerData;
import integral.studios.hydro.model.emulator.data.BruteforceData;
import integral.studios.hydro.model.emulator.data.EmulationData;

public interface Handler {
    EmulationData process(final BruteforceData data, final PlayerData playerData, final EmulationData emulationData);
}