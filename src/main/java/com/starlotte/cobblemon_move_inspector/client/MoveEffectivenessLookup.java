package com.starlotte.cobblemon_move_inspector.client;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.types.ElementalType;

import java.util.*;

public class MoveEffectivenessLookup {
    private static final HashMap<String, HashMap<String, Integer>> typeChart = new HashMap<>();
    private static final GraalTypeChartGetter typeChartGetter = new GraalTypeChartGetter();

    public static float getModifier(MoveTemplate move, ElementalType defenderType1, ElementalType defenderType2) {
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
