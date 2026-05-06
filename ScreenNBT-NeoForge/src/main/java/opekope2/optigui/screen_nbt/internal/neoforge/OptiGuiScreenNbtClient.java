package opekope2.optigui.screen_nbt.internal.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import opekope2.optigui.interaction.nbt_provider.IInteractionNbtProvider;
import opekope2.optigui.screen_nbt.nbt_provider.ScreenNbtProvider;

@Mod(value = OptiGuiScreenNbtClient.MOD_ID, dist = Dist.CLIENT)
public class OptiGuiScreenNbtClient {
    public static final String MOD_ID = "optigui_screen_nbt";

    public OptiGuiScreenNbtClient() {
        IInteractionNbtProvider.Registry.register("screen", new ScreenNbtProvider());
    }
}
