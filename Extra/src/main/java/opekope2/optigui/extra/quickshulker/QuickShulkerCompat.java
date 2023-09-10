package opekope2.optigui.extra.quickshulker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.kyrptonaught.quickshulker.api.Util;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import opekope2.lilac.api.registry.IRegistryLookup;
import opekope2.optigui.api.IOptiGuiApi;
import opekope2.optigui.api.interaction.IInteractionTarget;
import opekope2.optigui.api.interaction.IInteractor;
import opekope2.optigui.properties.impl.CommonProperties;
import opekope2.optigui.properties.impl.GeneralProperties;
import opekope2.optigui.properties.impl.IndependentProperties;

import java.time.LocalDate;
import java.util.function.Consumer;

public class QuickShulkerCompat implements ClientModInitializer, UseItemCallback {
    private static final IInteractor interactor = IOptiGuiApi.getImplementation().getInteractor();
    private static final IRegistryLookup lookup = IRegistryLookup.getInstance();
    private static Consumer<Screen> screenChangeHandler = QuickShulkerCompat::dummyScreenConsumer;

    @Override
    public void onInitializeClient() {
        if (!FabricLoader.getInstance().isModLoaded("quickshulker")) return;

        UseItemCallback.EVENT.register(this);
    }

    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) {
            if (QuickShulkerMod.getConfig().rightClickToOpen && Util.isOpenableItem(stack) && Util.canOpenInHand(stack)) {
                triggerInteraction(player, world, hand, stack);
            }
        }

        return TypedActionResult.pass(stack);
    }

    public static void waitForScreen(Consumer<Screen> handler) {
        screenChangeHandler = handler;
    }

    public static void onScreenChanged(Screen screen) {
        screenChangeHandler.accept(screen);
        screenChangeHandler = QuickShulkerCompat::dummyScreenConsumer;
    }

    private static void dummyScreenConsumer(Screen screen) {
    }

    public static void triggerInteraction(PlayerEntity player, World world, Hand hand, ItemStack stack) {
        interactor.interact(
                player,
                world,
                hand,
                new IInteractionTarget.ComputedTarget(getInteractionTargetData(player, world, stack)),
                null
        );
    }

    private static Object getInteractionTargetData(PlayerEntity player, World world, ItemStack stack) {
        Identifier biome = lookup.lookupBiomeId(world, player.getBlockPos());
        String name = stack.hasCustomName() ? stack.getName().getString() : null;

        return new CommonProperties(
                new GeneralProperties(
                        Registries.ITEM.getId(stack.getItem()),
                        name,
                        biome,
                        player.getBlockY()
                ),
                new IndependentProperties(
                        LocalDate.now()
                )
        );
    }
}
