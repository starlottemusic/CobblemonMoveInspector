package com.starlotte.cobblemon_move_inspector.neoforge;

import com.starlotte.cobblemon_move_inspector.CobblemonMoveInspector;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CobblemonMoveInspector.MOD_ID)
public final class cobblemon_move_inspector {
    public cobblemon_move_inspector(IEventBus modBus) {
        // Run our common setup.
        CobblemonMoveInspector.init();
    }
}