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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MoveEffectivenessLookup {
    private static final HashMap<String, HashMap<String, Integer>> typeChart = new HashMap<>();
    private static final GraalTypeChartGetter typeChartGetter = new GraalTypeChartGetter();

    /**
     * Figures out how affective a move will be based off the move's type & the
     * target Pokemon's type(s)
     * 
     * @param move          Hovered [MoveTemplate]
     * @param defenderType1 Targetted Pokemon's Primary [ElementalType]
     * @param defenderType2 Targetted Pokemon's Secondary [ElementalType]
     * @param player        [UUID] Unsure why it's being passed through-
     * 
     * @return the multiplier that the move will do to the target Pokemon
     */
    public static float getModifier(MoveTemplate move, ElementalType defenderType1, ElementalType defenderType2,
            UUID player) {
        // Switching how modifier to handled based off Damage Category
        if (move.getDamageCategory().equals("status")) {
                return getStatusModifier(move, defenderType1, defenderType2);
        }
        else{
                return getAttackModifier(move, defenderType1, defenderType2);
        }
    }

    /**
     * Modifier for Status Moves on whether it will be Immune or not. (Needs to be
     * tested with other languages)
     * 
     * @param move          Hovered [MoveTemplate]
     * @param defenderType1 Targetted Pokemon's Primary [ElementalType]
     * @param defenderType2 Targetted Pokemon's Secondary [ElementalType]
     * 
     * @return multiplier applied to attack when used against target Pokemon
     * 
     * @author LukyStudios
     */
    public static float getStatusModifier(MoveTemplate move, ElementalType defenderType1, ElementalType defenderType2) {
        ElementalType moveType = move.getElementalType();
        float damageMult = 1;

        // Ignore if not targetting opponent
        if (move.getTarget().equals("self")) {
            return damageMult;
        }

        // Grass Case (...Powder, ...Spore, Leed Seed)
        if (matchesType(defenderType1, defenderType2, "grass")) {
            Pattern powderPattern = Pattern.compile("powder", Pattern.CASE_INSENSITIVE);
            Matcher powderMatcher = powderPattern.matcher(move.getName());

            Pattern sporePattern = Pattern.compile("spore", Pattern.CASE_INSENSITIVE);
            Matcher sporeMatcher = sporePattern.matcher(move.getName());

            // Funk with Languages prob
            if (powderMatcher.find() || sporeMatcher.find() || move.getName().equals("leech seed")) {
                damageMult *= 0;
            }
        }

        // Fire Case (Burn Effect)
        if (matchesType(defenderType1, defenderType2, "fire")){

        }

        // Poison Case (Poison Effect)
        // Steel Case (Poison Effect)
        if (matchesType(defenderType1, defenderType2, "poison") 
                || matchesType(defenderType1, defenderType2, "steel")) {
            
        }

        // Electric Case (Paralysis Effect)
        if (matchesType(defenderType1, defenderType2, "electric")){
            
        }

        // Ground Case (Thunder Wave)
        if (matchesType(defenderType1, defenderType2, "ground")) {
            // Funk with Languages prob
            if(move.getName().equals("thunder wave")) {
                damageMult *= 0;
            }
        }

        return damageMult;
    }

    /**
     * Checks to see if either of a Pokemon's type matches the desired type
     * 
     * @param type1 Pokemon's Primary [ElementalType]
     * @param type2 Pokemon's Secondary [ElementalType]
     * @param targetType String of Type
     * 
     * @return if either type matches
     * 
     * @author LukyStudios
     */
    private static boolean matchesType(ElementalType type1, ElementalType type2, String targetType) {
        return type1 != null ? type1.getName().equals(targetType) : false 
                || type2 != null ? type2.getName().equals(targetType) : false;
    }

    /**
     * Legacy Modifier for Attacking moves
     * 
     * @param move          Hovered [MoveTemplate]
     * @param defenderType1 Targetted Pokemon's Primary [ElementalType]
     * @param defenderType2 Targetted Pokemon's Secondary [ElementalType]
     * 
     * @return multiplier applied to attack when used against target Pokemon
     * 
     * @author Starlotte
     */
    public static float getAttackModifier(MoveTemplate move, ElementalType defenderType1, ElementalType defenderType2) {
        ElementalType moveType = move.getElementalType();
        float damageMult = 1;

        // Check special conditions for move (eg. steel immune to sandstorm)
        if (move != null) {
            if (defenderType1 != null) {
                damageMult *= getMultFromType(move.getName(), defenderType1.getName());
            }
            if (defenderType2 != null) {
                damageMult *= getMultFromType(move.getName(), defenderType2.getName());
            }
        }

        // Check type matchup
        if (defenderType1 != null) {
            damageMult *= getMultFromType(moveType.getName(), defenderType1.getName());
        }
        if (defenderType2 != null) {
            damageMult *= getMultFromType(moveType.getName(), defenderType2.getName());
        }

        return damageMult;
    }

    public static float getMultFromType(String moveName, String typeName) {
        HashMap<String, Integer> matchupMap = typeChart.get(typeName);
        if (matchupMap == null) {
            return 1;
        }

        Integer damageType = matchupMap.get(moveName);
        if (damageType == null) {
            return 1;
        }

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
