package com.starlotte.cobblemon_move_inspector.client;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.data.JsonDataRegistry;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.pokedex.DexAdditions;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.api.pokedex.entry.DexEntries;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData;
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexManager;
import com.cobblemon.mod.common.api.storage.player.client.ClientSpeciesPokedexRecord;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokedex.scanner.PokedexEntityData;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;

public class MoveEffectivenessLookup {
    private static final HashMap<String, HashMap<String, Integer>> typeChart = new HashMap<>();
    private static final GraalTypeChartGetter typeChartGetter = new GraalTypeChartGetter();

    public static float getModifier(MoveTemplate move, ElementalType defenderType1, ElementalType defenderType2, UUID player) {
        ElementalType moveType = move.getElementalType();
        float damageMult = 1;

        // Check special conditions for move (eg. steel immune to sandstorm)
        if (move != null) {
            if (defenderType1 != null) damageMult *= getMultFromType(move.getName(), defenderType1.getName());
            if (defenderType2 != null) damageMult *= getMultFromType(move.getName(), defenderType2.getName());
        }
        // Check type matchup
        if (defenderType1 != null) damageMult *= getMultFromType(moveType.getName(), defenderType1.getName());
        if (defenderType2 != null) damageMult *= getMultFromType(moveType.getName(), defenderType2.getName());

        return damageMult;
    }

    public static float getMultFromType(String moveName, String typeName) {
        HashMap<String, Integer> matchupMap = typeChart.get(typeName);
        if (matchupMap == null)
            return 1;

        Integer damageType = matchupMap.get(moveName);
        if (damageType == null)
            return 1;

        return getMult(damageType);
    }

    public static float getMult(int damage) {
        return switch (damage) {
            default -> 1;
            case (1) -> 2;
            case (2) -> 0.5f;
            case (3) -> 0;
        };
    }

    static {
        typeChartGetter.openConnection();
        typeChartGetter.getTypeChart(typeChart);
    }
}
