package opekope2.optigui.screen_nbt.internal.fabric;

import net.fabricmc.api.ClientModInitializer;
import opekope2.optigui.interaction.nbt_provider.IInteractionNbtProvider;
import opekope2.optigui.screen_nbt.nbt_provider.ScreenNbtProvider;

public class OptiGuiScreenNbtClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IInteractionNbtProvider.Registry.register("screen", new ScreenNbtProvider());
    }
}
