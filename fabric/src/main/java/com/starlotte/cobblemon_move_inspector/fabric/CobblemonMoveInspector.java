package com.starlotte.cobblemon_move_inspector.fabric;

import net.fabricmc.api.ModInitializer;

public final class CobblemonMoveInspector implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        com.starlotte.cobblemon_move_inspector.CobblemonMoveInspector.init();
    }
}
