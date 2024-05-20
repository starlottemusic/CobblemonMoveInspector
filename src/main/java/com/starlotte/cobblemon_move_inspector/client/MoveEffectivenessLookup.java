package com.starlotte.cobblemon_move_inspector.client;

import com.cobblemon.mod.common.api.gui.MultiLineLabelK;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import drai.dev.gravelsextendedbattles.fabric.mixin.ElementalTypesMixin;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class MoveEffectivenessLookup {

    private static final Map<ElementalType, List<ElementalType>> weakMap = new HashMap<>();
    private static final Map<ElementalType, List<ElementalType>> strongMap = new HashMap<>();
    private static final Map<ElementalType, List<ElementalType>> immuneMap = new HashMap<>();

    public static float getModifier(ElementalType moveType, ElementalType defenderType1, ElementalType defenderType2) {
        float attackMultiplier = 1;

        List<ElementalType> weak = weakMap.get(moveType);
        List<ElementalType> strong = strongMap.get(moveType);
        List<ElementalType> immune = immuneMap.get(moveType);

        // If a custom type isn't registered, ignore the effectiveness check
        if (weak == null) return attackMultiplier;

        if (defenderType1 != null) {
        if (weak.contains(defenderType1))
            attackMultiplier /= 2;

        if (strong.contains(defenderType1))
            attackMultiplier *= 2;

        if (immune.contains(defenderType1))
            attackMultiplier = 0;
    }

        if (defenderType2 != null) {
            if (weak.contains(defenderType2))
                attackMultiplier /= 2;

            if (strong.contains(defenderType2))
                attackMultiplier *= 2;

            if (immune.contains(defenderType2))
                attackMultiplier = 0;
        }

        return attackMultiplier;
    }

    @NotNull
    private static ElementalTypes instance() {
        return ElementalTypes.INSTANCE;
    }

    private static void addToMap(Map<ElementalType, List<ElementalType>> map, ElementalType type, ElementalType... types) {
        map.put(type, List.of(types));
    }

    private static void appendMap(Map<ElementalType, List<ElementalType>> map, ElementalType type, ElementalType... types) {
        List<ElementalType> allTypes = new ArrayList<>();
        allTypes.addAll(map.get(type));
        allTypes.addAll(List.of(types));
        map.put(type, allTypes);
    }

    static {
        ElementalType normal = instance().getNORMAL();
        ElementalType fire = instance().getFIRE();
        ElementalType water = instance().getWATER();
        ElementalType grass = instance().getGRASS();
        ElementalType electric = instance().getELECTRIC();
        ElementalType ice = instance().getICE();
        ElementalType fighting = instance().getFIGHTING();
        ElementalType poison = instance().getPOISON();
        ElementalType ground = instance().getGROUND();
        ElementalType flying = instance().getFLYING();
        ElementalType psychic = instance().getPSYCHIC();
        ElementalType bug = instance().getBUG();
        ElementalType rock = instance().getROCK();
        ElementalType ghost = instance().getGHOST();
        ElementalType dragon = instance().getDRAGON();
        ElementalType dark = instance().getDARK();
        ElementalType steel = instance().getSTEEL();
        ElementalType fairy = instance().getFAIRY();

        // ATTACKER IS WEAK AGAINST
        addToMap(weakMap, normal, rock, steel);
        addToMap(weakMap, fire, fire, water, rock, dragon);
        addToMap(weakMap, water, water, grass, dragon);
        addToMap(weakMap, grass, fire, grass, poison, flying, bug, dragon, steel);
        addToMap(weakMap, electric);
        addToMap(weakMap, ice, fire, water, ice, steel);
        addToMap(weakMap, fighting, poison, flying, psychic, bug, fairy);
        addToMap(weakMap, poison, poison, ground, rock, ghost);
        addToMap(weakMap, ground, grass, bug);
        addToMap(weakMap, flying, electric, rock, steel);
        addToMap(weakMap, psychic, psychic, steel);
        addToMap(weakMap, bug, fire, fighting, poison, flying, ghost, steel, fairy);
        addToMap(weakMap, rock, fighting, flying, steel);
        addToMap(weakMap, ghost, dark);
        addToMap(weakMap, dragon, steel);
        addToMap(weakMap, dark, fighting, dark, fairy);
        addToMap(weakMap, steel, fire, water, electric, steel);
        addToMap(weakMap, fairy, fire, poison, steel);

        // ATTACKER IS STRONG AGAINST
        addToMap(strongMap, normal);
        addToMap(strongMap, fire, grass, ice, bug, steel);
        addToMap(strongMap, water, fire, ground, rock);
        addToMap(strongMap, grass, water, ground, rock);
        addToMap(strongMap, electric, water, ground, rock);
        addToMap(strongMap, ice, grass, ground, flying, dragon);
        addToMap(strongMap, fighting, normal, ice, rock, dark, steel);
        addToMap(strongMap, poison, grass, fairy);
        addToMap(strongMap, ground, fire, electric, poison, rock, steel);
        addToMap(strongMap, flying, grass, fighting, bug);
        addToMap(strongMap, psychic, fighting, poison);
        addToMap(strongMap, bug, grass, psychic, dark);
        addToMap(strongMap, rock, fire, ice, flying, bug);
        addToMap(strongMap, ghost, psychic, ghost);
        addToMap(strongMap, dragon, dragon);
        addToMap(strongMap, dark, psychic, ghost);
        addToMap(strongMap, steel, ice, rock, fairy);
        addToMap(strongMap, fairy, fighting, ghost, dragon);


        // ATTACKER CANNOT HIT
        addToMap(immuneMap, normal, ghost);
        addToMap(immuneMap, fire);
        addToMap(immuneMap, water);
        addToMap(immuneMap, grass);
        addToMap(immuneMap, electric, ground);
        addToMap(immuneMap, ice);
        addToMap(immuneMap, fighting, ghost);
        addToMap(immuneMap, poison, steel);
        addToMap(immuneMap, ground, flying);
        addToMap(immuneMap, flying);
        addToMap(immuneMap, psychic, dark);
        addToMap(immuneMap, bug);
        addToMap(immuneMap, rock);
        addToMap(immuneMap, ghost, normal);
        addToMap(immuneMap, dragon, fairy);
        addToMap(immuneMap, dark);
        addToMap(immuneMap, steel);
        addToMap(immuneMap, fairy);


        // This whole system will need a complete overhaul when cobblemon adds custom type support lol
        // GRAVELMON
        if (FabricLoader.getInstance().isModLoaded("gravels_extended_battles")) {
            ElementalType shadow = ElementalTypes.INSTANCE.get("shadow");
            ElementalType questionmark = ElementalTypes.INSTANCE.get("questionmark");
            ElementalType cosmic = ElementalTypes.INSTANCE.get("cosmic");
            ElementalType crystal = ElementalTypes.INSTANCE.get("crystal");
            ElementalType digital = ElementalTypes.INSTANCE.get("digital");
            ElementalType light = ElementalTypes.INSTANCE.get("light");
            ElementalType nuclear = ElementalTypes.INSTANCE.get("nuclear");
            ElementalType plastic = ElementalTypes.INSTANCE.get("plastic");
            ElementalType slime = ElementalTypes.INSTANCE.get("slime");
            ElementalType sound = ElementalTypes.INSTANCE.get("sound");
            ElementalType wind = ElementalTypes.INSTANCE.get("wind");
            ElementalType eldritch = ElementalTypes.INSTANCE.get("eldritch");

            // ATTACKER IS WEAK AGAINST
            appendMap(weakMap, normal, eldritch);
            appendMap(weakMap, fire, cosmic, light, wind);
            appendMap(weakMap, water, slime);
            appendMap(weakMap, grass, plastic);
            appendMap(weakMap, electric, light, plastic);
            appendMap(weakMap, ice, cosmic);
            appendMap(weakMap, fighting, slime);
            appendMap(weakMap, poison, nuclear, plastic);
            appendMap(weakMap, ground, slime, wind);
            appendMap(weakMap, flying, sound, wind);
            appendMap(weakMap, psychic, digital);
            appendMap(weakMap, bug, plastic, slime);
            appendMap(weakMap, rock, cosmic);
            appendMap(weakMap, ghost, digital, light, sound);
            appendMap(weakMap, dragon, light);
            appendMap(weakMap, dark, digital);
//            appendMap(weakMap, steel);
            appendMap(weakMap, fairy, digital, plastic, sound);

            addToMap(weakMap, shadow, shadow, light);
            addToMap(weakMap, questionmark);
            addToMap(weakMap, cosmic, ice, ground, psychic, cosmic, digital);
            addToMap(weakMap, crystal, rock, crystal);
            addToMap(weakMap, digital, electric, psychic, bug, dark, eldritch);
            addToMap(weakMap, light, grass, ice, cosmic, digital, light);
            addToMap(weakMap, nuclear, bug, rock, steel);
            addToMap(weakMap, plastic, fire, fighting, poison, ground, rock);
            addToMap(weakMap, slime, water, poison, steel, plastic, eldritch);
            addToMap(weakMap, sound, grass, ground, bug, dragon, slime, sound, wind);
            addToMap(weakMap, wind, fire, steel, wind);
            addToMap(weakMap, eldritch, psychic, cosmic, crystal, light);

            // ATTACKER IS STRONG AGAINST
            appendMap(strongMap, normal, shadow);
            appendMap(strongMap, fire, shadow, nuclear, plastic, slime);
            appendMap(strongMap, water, shadow, nuclear);
            appendMap(strongMap, grass, shadow, light);
            appendMap(strongMap, electric, shadow, digital, sound);
            appendMap(strongMap, ice, shadow, nuclear, plastic, wind);
            appendMap(strongMap, fighting, shadow, crystal);
            appendMap(strongMap, poison, shadow, slime);
            appendMap(strongMap, ground, shadow, nuclear);
            appendMap(strongMap, flying, shadow);
            appendMap(strongMap, psychic, shadow, cosmic, sound);
            appendMap(strongMap, bug, shadow, digital);
            appendMap(strongMap, rock, shadow);
            appendMap(strongMap, ghost, shadow);
            appendMap(strongMap, dragon, shadow, sound);
            appendMap(strongMap, dark, shadow, light);
            appendMap(strongMap, steel, shadow, cosmic, light, nuclear);
            appendMap(strongMap, fairy, shadow);

            addToMap(strongMap, shadow, normal, fire, water, grass, electric, ice, fighting, poison, ground, flying, psychic, bug, rock, ghost, dragon, dark, steel, fairy, cosmic, digital, nuclear, plastic, slime, sound, wind, eldritch);
            addToMap(strongMap, questionmark);
            addToMap(strongMap, cosmic, shadow, dragon, sound, wind, eldritch);
            addToMap(strongMap, crystal, light);
            addToMap(strongMap, digital, shadow, ghost, fairy, cosmic, digital, nuclear);
            addToMap(strongMap, light, shadow, flying, ghost, dark, eldritch);
            addToMap(strongMap, nuclear, shadow, normal, water, grass, fighting, ground, fairy, slime);
            addToMap(strongMap, plastic, shadow, water, electric, slime);
            addToMap(strongMap, slime, shadow, bug, digital);
            addToMap(strongMap, sound, shadow, fighting, flying, psychic, dark, fairy, digital);
            addToMap(strongMap, wind, shadow, poison, ground, bug, sound);
            addToMap(strongMap, eldritch, shadow, normal, fairy, digital);

            // ATTACKER CANNOT HIT
//             appendMap(immuneMap, normal);
            appendMap(immuneMap, fire, eldritch);
//            appendMap(immuneMap, water);
//            appendMap(immuneMap, electric);
//            appendMap(immuneMap, grass);
//            appendMap(immuneMap, ice);
//            appendMap(immuneMap, fighting);
//            appendMap(immuneMap, poison);
//            appendMap(immuneMap, flying);
//            appendMap(immuneMap, ground);
//            appendMap(immuneMap, psychic);
//            appendMap(immuneMap, bug);
//            appendMap(immuneMap, rock);
//            appendMap(immuneMap, ghost);
//            appendMap(immuneMap, dragon);
//            appendMap(immuneMap, dark);
//            appendMap(immuneMap, steel);
//            appendMap(immuneMap, fairy);

            addToMap(immuneMap, shadow);
            addToMap(immuneMap, questionmark);
            addToMap(immuneMap, cosmic);
            addToMap(immuneMap, crystal);
            addToMap(immuneMap, digital);
            addToMap(immuneMap, light, crystal);
            addToMap(immuneMap, nuclear, nuclear);
            addToMap(immuneMap, plastic);
            addToMap(immuneMap, slime);
            addToMap(immuneMap, sound, cosmic);
            addToMap(immuneMap, wind, cosmic);
            addToMap(immuneMap, eldritch);
        }
    }
}
