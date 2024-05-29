package com.starlotte.cobblemon_move_inspector.mixin.client;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.battles.MoveTarget;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.battle.ClientBattle;
import com.cobblemon.mod.common.client.battle.ClientBattleActor;
import com.cobblemon.mod.common.client.battle.ClientBattlePokemon;
import com.cobblemon.mod.common.client.gui.battle.subscreen.BattleMoveSelection.MoveTile;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.starlotte.cobblemon_move_inspector.client.MoveEffectivenessLookup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(MoveTile.class)
public abstract class BattleMoveSelectionGUIMixin {

    @Shadow
    public abstract boolean isHovered(double mouseX, double mouseY);

    @Shadow
    public MoveTemplate moveTemplate;

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void tooltipRenderMixin(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) throws ScriptException, IOException {
        // This whole thing is SO janky lmfao
        if (this.isHovered(mouseX, mouseY)) {
            List<Text> tooltipInfo = new ArrayList<>();

            List<StringVisitable> description = MinecraftClient.getInstance().textRenderer.getTextHandler().wrapLines(
                    moveTemplate.getDescription(), 250, Style.EMPTY.withColor(moveTemplate.getElementalType().getHue()));

            for (StringVisitable stringVisitable : description) {
                tooltipInfo.add(MutableText.of(new LiteralTextContent(stringVisitable.getString())).formatted(Formatting.GRAY));
            }

            if (moveTemplate.getDamageCategory() != DamageCategories.INSTANCE.getSTATUS() || moveTemplate.getAccuracy() != -1.0 || moveTemplate.getEffectChances().length != 0) {
                tooltipInfo.add(Text.of(""));
                if (moveTemplate.getPower() > 0)
                    tooltipInfo.add(Text.of("Power: " + (int) moveTemplate.getPower()));
                if (moveTemplate.getAccuracy() > 0)
                    tooltipInfo.add(Text.of("Accuracy: " + (int) moveTemplate.getAccuracy() + "%"));
                if (moveTemplate.getEffectChances().length != 0)
                    tooltipInfo.add(Text.of("Effect: " + Math.round(moveTemplate.getEffectChances()[0]) + "%"));
            }

            ClientBattle battle = CobblemonClient.INSTANCE.getBattle();

            if (battle != null) {
                List<ClientBattleActor> enemies = battle.getSide2().getActors();
                if (!enemies.isEmpty() && moveTemplate.getTarget() != MoveTarget.self) {
                    ClientBattleActor firstPlayer = enemies.get(0);
                    ClientBattlePokemon firstActivePokemon = firstPlayer.getActivePokemon().get(0).getBattlePokemon();

                    Pokemon properties = firstActivePokemon.getProperties().create();
                    ElementalType opponentFirstType = properties.getPrimaryType();
                    ElementalType opponentSecondType = properties.getSecondaryType();

                    float effectiveness = MoveEffectivenessLookup.getModifier(moveTemplate, opponentFirstType, opponentSecondType);
                    if (effectiveness == 0) {
                        tooltipInfo.add(MutableText.of(new LiteralTextContent("Immune")).formatted(Formatting.ITALIC, Formatting.GRAY));
                    } else if (effectiveness > 1) {
                        tooltipInfo.add(MutableText.of(new LiteralTextContent(effectiveness + "x Super Effective")).formatted(Formatting.BOLD, Formatting.GOLD));
                    } else if (effectiveness < 1) {
                        tooltipInfo.add(MutableText.of(new LiteralTextContent(effectiveness + "x Ineffective")).formatted(Formatting.ITALIC, Formatting.GRAY));
                    }
                }
            }

            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltipInfo, mouseX, mouseY);
        }
    }
}
