package opekope2.optigui.extra.quickshulker.mixin;

import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import opekope2.lilac.api.registry.IRegistryLookup;
import opekope2.optigui.extra.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;

import static opekope2.optigui.extra.quickshulker.QuickShulkerRightClickHandler.triggerInteraction;
import static opekope2.optigui.extra.quickshulker.QuickShulkerRightClickHandler.waitForScreen;

@Mixin(ClientUtil.class)
public abstract class ClientUtilMixin {
    @Unique
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    @Unique
    private static final IRegistryLookup lookup = IRegistryLookup.getInstance();

    @Unique
    private static final Map<Identifier, Class<? extends HandledScreen<?>>> containerScreenMapping = Util.buildMap(map -> {
        map.put(new Identifier("shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("white_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("orange_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("magenta_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("light_blue_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("yellow_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("lime_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("pink_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("gray_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("light_gray_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("cyan_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("purple_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("blue_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("brown_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("green_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("red_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("black_shulker_box"), ShulkerBoxScreen.class);
        map.put(new Identifier("ender_chest"), GenericContainerScreen.class);
        map.put(new Identifier("crafting_table"), CraftingScreen.class);
        map.put(new Identifier("stonecutter"), StonecutterScreen.class);
        map.put(new Identifier("smithing_table"), SmithingScreen.class);
    });

    @Inject(
            method = "CheckAndSend",
            at = @At(value = "INVOKE", target = "Lnet/kyrptonaught/quickshulker/client/ClientUtil;SendOpenPacket(I)V")
    )
    private static void handlePacketSendFromOffHand(ItemStack stack, int slot, CallbackInfoReturnable<Boolean> cir) {
        var player = Objects.requireNonNull(mc.player);
        var world = Objects.requireNonNull(mc.world);
        var screenClass = containerScreenMapping.get(lookup.lookupItemId(stack.getItem()));
        if (screenClass == null) return;

        waitForScreen(screen -> {
            if (screenClass.equals(screen.getClass())) {
                triggerInteraction(player, world, stack == mc.player.getOffHandStack() ? Hand.OFF_HAND : Hand.MAIN_HAND, stack);
            }
        });
    }
}
