package opekope2.optigui.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.SectionHeader;
import opekope2.optigui.interaction.IInteraction;

import static opekope2.optigui.util.Constants.MOD_ID;

/**
 * OptiGUI config file model.
 */
@Config(name = MOD_ID, wrapperName = "Config")
public class ConfigModel {
    /**
     * Shows a button on every supported GUI screen to inspect the ongoing interaction.
     */
    @SectionHeader("inspector")
    public boolean enableInspector = true;
    /**
     * Includes NBT data in the generated JSON filter resources.
     */
    public InspectorNbtDumper dumpNbt = InspectorNbtDumper.NOTHING;

    /**
     * Attacking blocks, entities, or in the air also starts an interaction.
     */
    @SectionHeader("interaction")
    public boolean interactWithAttackKey = false;
    /**
     * Keep {@link IInteraction.IFactory} after an interaction ends.
     */
    public boolean keepInteractionFactory = false;

    /**
     * If there are problems loading resources, shows a screen with the details.
     */
    @SectionHeader("resourceLoading")
    public ResourceLoadingLogFilter showResourceLoadingErrors = ResourceLoadingLogFilter.ERRORS_ONLY;
}
