package opekope2.optigui.mixin;

import net.minecraft.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessorMixin {
    @Accessor
    int getLevel();
}
