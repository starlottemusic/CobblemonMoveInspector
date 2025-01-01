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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.util.FormattedCharSequence;
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

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void tooltipRenderMixin(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) throws ScriptException, IOException {
        // This whole thing is SO janky lmfao
        if (this.isHovered(mouseX, mouseY)) {
            List<Component> tooltipInfo = new ArrayList<>();

            List<FormattedText> description = Minecraft.getInstance().font.getSplitter().splitLines(
                    moveTemplate.getDescription(), 250, Style.EMPTY.withColor(moveTemplate.getElementalType().getHue()));

            for (FormattedText formattedText : description) {
                tooltipInfo.add(MutableComponent.create(new PlainTextContents.LiteralContents(formattedText.getString())).withStyle(ChatFormatting.GRAY));
            }

            if (moveTemplate.getDamageCategory() != DamageCategories.INSTANCE.getSTATUS() || moveTemplate.getAccuracy() != -1.0 || moveTemplate.getEffectChances().length != 0) {
                tooltipInfo.add(Component.empty());
                if (moveTemplate.getPower() > 0)
                    tooltipInfo.add(Component.translatable("move.inspector.power", (int) moveTemplate.getPower()));
                if (moveTemplate.getAccuracy() > 0)
                    tooltipInfo.add(Component.translatable("move.inspector.accuracy", (int) moveTemplate.getAccuracy()));
                if (moveTemplate.getEffectChances().length != 0)
                    tooltipInfo.add(Component.translatable("move.inspector.effect",Math.round(moveTemplate.getEffectChances()[0])));
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

                    float effectiveness = MoveEffectivenessLookup.getModifier(moveTemplate, opponentFirstType, opponentSecondType, firstPlayer.getUuid());
                    if (effectiveness == 0) {
                        tooltipInfo.add(Component.translatable("move.inspector.immune").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
                    } else if (effectiveness > 1) {
                        tooltipInfo.add(Component.translatable("move.inspector.effective", effectiveness).withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
                    } else if (effectiveness < 1) {
                        tooltipInfo.add(Component.translatable("move.inspector.ineffective", effectiveness).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
                    }
                }
            }

            context.renderComponentTooltip(Minecraft.getInstance().font, tooltipInfo, mouseX, mouseY);
        }
    }
}
