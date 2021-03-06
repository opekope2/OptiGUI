package opekope2.optigui.optifinecompat;

import static opekope2.optigui.util.OptiFineParser.parseList;
import static opekope2.optigui.util.Util.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import org.apache.commons.io.FilenameUtils;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.resource.Resource;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import opekope2.optigui.OptiGUIClient;
import opekope2.optigui.optifinecompat.OptiFineResourceLoader.ResourceLoadContext;
import opekope2.optigui.util.*;

// https://optifine.readthedocs.io/custom_guis.html
// https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/custom_guis.properties
public final class OptiFineProperties {
    private static final Identifier[] EMPTY_ID_ARRAY = new Identifier[0];

    private static final EnumProperty<ChestType> CHEST_TYPE_ENUM = EnumProperty.of("type", ChestType.class);

    private final Map<String, ContainerRemapper> remappers = new HashMap<>();
    private final Map<Identifier, BlockMatcher> blockMatchers = new HashMap<>();
    private final Map<Identifier, EntityMatcher> entityMatchers = new HashMap<>();

    private static final String texturePathPrefix = "texture.";

    // region Initializers
    {
        remappers.put("chest", this::remapChest);
        remappers.put("dispenser", this::remapDispenser);
        remappers.put("furnace", this::remapFurnace);
        remappers.put("shulker_box", this::remapShulkerBlock);
        remappers.put("horse", this::remapHorse);
        remappers.put("villager", this::remapVillager);

        blockMatchers.put(ID.BARREL, this::matchesChest);
        blockMatchers.put(ID.BEACON, this::matchesBeacon);
        blockMatchers.put(ID.BLAST_FURNACE, this::matchesFurnace);
        blockMatchers.put(ID.CHEST, this::matchesChest);
        blockMatchers.put(ID.ENDER_CHEST, this::matchesChest);
        blockMatchers.put(ID.FURNACE, this::matchesFurnace);
        blockMatchers.put(ID.SMOKER, this::matchesFurnace);
        blockMatchers.put(ID.TRAPPED_CHEST, this::matchesChest);

        entityMatchers.put(ID.LLAMA, this::matchesLlama);
        entityMatchers.put(ID.VILLAGER, this::matchesVillager);
        entityMatchers.put(ID.WANDERING_TRADER, this::matchesVillager);
    }
    // endregion

    private Map<Identifier, Identifier> textureRemaps = new HashMap<>();

    private boolean isEntity = false;

    private Boolean large = null;
    private Boolean trapped = null;
    private Boolean christmas = null;
    private Boolean ender = null;

    // region OptiFine extensions
    private Boolean _barrel = null;
    // endregion

    private IRegexMatcher nameMatcher = null;
    private List<Identifier> biomes = null;
    private List<IntRange> heights = null;
    private List<IntRange> levels = null;
    private List<VillagerMatcher> professions = null;
    private List<String> colors = null;

    private Identifier[] ids = null;

    // region Construction
    private OptiFineProperties(ResourceLoadContext context) {
        String container = context.getProperties().getProperty("container", null);
        if (container == null) {
            return;
        }

        Identifier[] ids = ID_AUTO_MAPPING.getOrDefault(container, null);
        if (ids != null) {
            this.ids = ids;
        }

        ContainerRemapper remapper = remappers.getOrDefault(container, null);
        if (remapper != null) {
            remapper.remapContainer(context.getProperties());
        }

        loadProperties(context.getProperties());
        loadTextureRemaps(context);
    }

    private void loadProperties(Properties props) {
        String name = props.getProperty("name", null);
        if (name != null) {
            this.nameMatcher = OptiFineParser.parseRegex(name);
        }

        String biomes = props.getProperty("biomes", null);
        if (biomes != null) {
            this.biomes = OptiFineParser.parseIdentifierList(biomes);
        }

        String heights = props.getProperty("heights", null);
        if (heights != null) {
            this.heights = OptiFineParser.parseRangeList(heights);
        }

        String levels = props.getProperty("levels", null);
        if (levels != null) {
            this.levels = OptiFineParser.parseRangeList(levels);
        }

        String professions = props.getProperty("professions", null);
        if (professions != null) {
            this.professions = OptiFineParser.parseProfessionList(professions);
        }

        String colors = props.getProperty("colors", null);
        if (colors != null) {
            this.colors = OptiFineParser.parseList(colors);
        }
    }

    private void loadTextureRemaps(ResourceLoadContext ctx) {
        String texture = ctx.getProperties().getProperty("texture", null);
        String resFolder = ctx.getResourceId().toString();
        resFolder = resFolder.substring(resFolder.indexOf(":") + 1, resFolder.lastIndexOf("/"));

        if (texture != null) {
            Identifier id = PathResolver.resolve(resFolder, texture);
            Identifier foundId = ctx.findResource(id);
            if (foundId == null) {
                OptiGUIClient.logger.warn("Resource '{}' is missing!", id.toString());
            } else {
                textureRemaps.put(getTextureToRemap(ctx.getProperties()), foundId);
            }
        }

        for (var property : ctx.getProperties().entrySet()) {
            String key = (String) property.getKey();

            if (key.startsWith(texturePathPrefix)) {
                String value = (String) property.getValue();
                Identifier id = PathResolver.resolve(resFolder, value);
                Identifier foundId = ctx.findResource(id);
                if (foundId == null) {
                    OptiGUIClient.logger.warn("Resource '{}' is missing!", id.toString());
                } else {
                    String texturePath = key.substring(texturePathPrefix.length());
                    textureRemaps.put(PathResolver.resolve("textures/gui", texturePath), foundId);
                }
            }
        }
    }

    private Identifier getTextureToRemap(Properties properties) {
        String container = properties.getProperty("container", null);
        Function<Properties, Identifier> remapper = TEXTURE_REMAPPERS.get(container);
        return remapper != null ? remapper.apply(properties) : null;
    }
    // endregion

    // region Replacing
    public boolean matchesBlock(BlockPos pos) {
        if (isEntity) {
            return false;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (biomes != null && !biomes.contains(getBiomeId(pos))) {
            return false;
        }

        if (heights != null) {
            boolean matchesHeight = false;
            for (IntRange height : heights) {
                if (height.test(pos.getY())) {
                    matchesHeight = true;
                    break;
                }
            }
            if (!matchesHeight) {
                return false;
            }
        }

        if (nameMatcher != null) {
            BlockEntity entity = mc.world.getBlockEntity(pos);
            if (entity != null && entity instanceof Nameable nameable) {
                if (nameable.hasCustomName()) {
                    String customName = nameable.getCustomName().getString();
                    if (!nameMatcher.matches(customName)) {
                        return false;
                    }
                } else if (!nameMatcher.matches("")) {
                    return false;
                }
            }
        }

        BlockState state = mc.world.getBlockState(pos);
        Identifier blockId = Registry.BLOCK.getId(state.getBlock());

        if (!contains(ids, blockId)) {
            return false;
        }

        BlockMatcher matcher = blockMatchers.getOrDefault(blockId, null);
        if (matcher != null && !matcher.matchesBlock(pos)) {
            return false;
        }

        return true;
    }

    public boolean matchesEntity(Entity entity) {
        if (!isEntity) {
            return false;
        }

        BlockPos pos = entity.getBlockPos();
        if (biomes != null && !biomes.contains(getBiomeId(pos))) {
            return false;
        }

        if (heights != null) {
            boolean matchesHeight = false;
            for (IntRange height : heights) {
                if (height.test(pos.getY())) {
                    matchesHeight = true;
                    break;
                }
            }
            if (!matchesHeight) {
                return false;
            }
        }

        if (nameMatcher != null && !nameMatcher.matches(entity.getCustomName().getString())) {
            return false;
        }

        Identifier entityId = Registry.ENTITY_TYPE.getId(entity.getType());

        if (!contains(ids, entityId)) {
            return false;
        }

        EntityMatcher matcher = entityMatchers.getOrDefault(entityId, null);
        if (matcher != null && !matcher.matchesEntity(entity)) {
            return false;
        }

        return true;
    }

    public boolean matchesAnything() {
        if (ids != null) {
            return false;
        }

        MinecraftClient mc = MinecraftClient.getInstance();

        if (biomes != null && !biomes.contains(getBiomeId(mc.player.getBlockPos()))) {
            return false;
        }

        if (heights != null) {
            boolean matchesHeight = true;
            for (IntRange height : heights) {
                if (height.test(mc.player.getBlockPos().getY())) {
                    matchesHeight = true;
                    break;
                }
            }
            if (!matchesHeight) {
                return false;
            }
        }

        return true;
    }

    public boolean hasReplacement(Identifier original) {
        if (textureRemaps.containsKey(original)) {
            return true;
        }
        String namespace = original.getNamespace(), path = original.getPath();
        path = FilenameUtils.removeExtension(path);
        return textureRemaps.containsKey(new Identifier(namespace, path));
    }

    public Identifier getReplacementTexture(Identifier original) {
        if (textureRemaps.containsKey(original)) {
            return textureRemaps.getOrDefault(original, original);
        }
        String namespace = original.getNamespace(), path = original.getPath();
        path = FilenameUtils.removeExtension(path);
        return textureRemaps.getOrDefault(new Identifier(namespace, path), original);
    }
    // endregion

    // region Block remaps
    private void remapChest(Properties packProps) {
        large = getBoolean(packProps.getProperty("large", null));
        trapped = getBoolean(packProps.getProperty("trapped", null));
        christmas = getBoolean(packProps.getProperty("christmas", null));
        ender = getBoolean(packProps.getProperty("ender", null));
        _barrel = getBoolean(packProps.getProperty("_barrel", null));

        List<Identifier> variantList = new ArrayList<>();
        variantList.add(ID.CHEST);
        if (ender != null && ender) {
            variantList.add(ID.ENDER_CHEST);
        }
        if (trapped != null && trapped) {
            variantList.add(ID.TRAPPED_CHEST);
        }
        if (_barrel != null && _barrel) {
            variantList.add(ID.BARREL);
        }
        ids = variantList.toArray(EMPTY_ID_ARRAY);
    }

    private void remapDispenser(Properties properties) {
        String variants = properties.getProperty("variants", null);

        ids = variants == null
                ? new Identifier[] { ID.DISPENSER, ID.DROPPER }
                : switch (variants) {
                    case "", "dispenser" -> new Identifier[] { ID.DISPENSER };
                    case "dropper" -> new Identifier[] { ID.DROPPER };
                    default -> ids;
                };
    }

    private void remapFurnace(Properties properties) {
        String variants = properties.getProperty("variants", null);

        ids = variants == null
                ? new Identifier[] { ID.FURNACE, ID.BLAST_FURNACE, ID.SMOKER }
                : switch (variants) {
                    case "", "_furnace" -> new Identifier[] { ID.FURNACE };
                    case "_blast", "_blast_furnace" -> new Identifier[] { ID.BLAST_FURNACE };
                    case "_smoker" -> new Identifier[] { ID.SMOKER };
                    default -> ids;
                };
    }

    private void remapShulkerBlock(Properties properties) {
        String colors = properties.getProperty("colors", null);
        List<Identifier> ids = new ArrayList<>();

        if (colors == null) {
            for (Identifier shulker : COLOR_TO_SHULKER_MAPPING.values()) {
                ids.add(shulker);
            }
        } else {
            List<String> colorList = parseList(colors);
            for (String color : colorList) {
                Identifier shulker = COLOR_TO_SHULKER_MAPPING.getOrDefault(color, null);
                if (shulker != null) {
                    ids.add(shulker);
                }
            }
        }

        // C# is smarter than this
        this.ids = ids.toArray(EMPTY_ID_ARRAY);
    }
    // endregion

    // region Entity remaps
    private void remapHorse(Properties properties) {
        isEntity = true;
        String variants = properties.getProperty("variants", null);
        if (variants == null) {
            ids = new Identifier[] { ID.HORSE, ID.DONKEY, ID.MULE, ID.LLAMA };
            return;
        }
        ids = switch (variants) {
            case "horse" -> new Identifier[] { ID.HORSE };
            case "donkey" -> new Identifier[] { ID.DONKEY };
            case "mule" -> new Identifier[] { ID.MULE };
            case "llama" -> new Identifier[] { ID.LLAMA };
            default -> ids;
        };
    }

    private void remapVillager(Properties properties) {
        isEntity = true;
        ids = new Identifier[] { ID.VILLAGER, ID.WANDERING_TRADER };
    }
    // endregion

    // region Block matchers
    private boolean matchesChest(BlockPos pos) {
        MinecraftClient mc = MinecraftClient.getInstance();
        BlockState state = mc.world.getBlockState(pos);
        Identifier id = Registry.BLOCK.getId(state.getBlock());
        Comparable<?> type = state.getEntries().get(CHEST_TYPE_ENUM);

        boolean matchesLarge = large == null || (type == null || large != type.equals(ChestType.SINGLE));
        boolean matchesChristmas = christmas == null ? true : christmas == isChristmas();

        if (ID.CHEST.equals(id)) {
            return matchesLarge && matchesChristmas && !Boolean.TRUE.equals(trapped) && !Boolean.TRUE.equals(ender)
                    && !Boolean.TRUE.equals(_barrel);
        } else if (ID.TRAPPED_CHEST.equals(id)) {
            return matchesLarge && matchesChristmas && !Boolean.FALSE.equals(trapped) && !Boolean.TRUE.equals(ender)
                    && !Boolean.TRUE.equals(_barrel);
        } else if (ID.ENDER_CHEST.equals(id)) {
            return matchesLarge && matchesChristmas && !Boolean.TRUE.equals(trapped) && !Boolean.FALSE.equals(ender)
                    && !Boolean.TRUE.equals(_barrel);
        } else if (ID.BARREL.equals(id)) {
            return matchesLarge && matchesChristmas && !Boolean.TRUE.equals(trapped) && !Boolean.TRUE.equals(ender)
                    && !Boolean.FALSE.equals(_barrel);
        }
        return false;
    }

    private boolean matchesBeacon(BlockPos pos) {
        if (levels == null) {
            return true;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        BlockEntity entity = mc.world.getBlockEntity(pos);
        int beaconLevel = entity.createNbt().getInt("Levels");

        for (IntRange level : levels) {
            if (level.test(beaconLevel)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesFurnace(BlockPos pos) {
        MinecraftClient mc = MinecraftClient.getInstance();
        BlockState state = mc.world.getBlockState(pos);
        Identifier id = Registry.BLOCK.getId(state.getBlock());

        return ID.BLAST_FURNACE.equals(id) || ID.FURNACE.equals(id) || ID.SMOKER.equals(id);
    }
    // endregion

    // region Entity matchers
    private boolean matchesLlama(Entity entity) {
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        NbtElement nbtDecor = nbt.get("DecorItem");

        if (nbtDecor != null && nbtDecor instanceof NbtCompound compound) {
            String carpet = compound.getString("id");
            if (carpet == null) {
                return colors.isEmpty();
            }

            carpet = CARPET_TO_COLOR_MAPPING.getOrDefault(carpet, null);
            return carpet != null && colors.contains(carpet);
        }
        return false;
    }

    private boolean matchesVillager(Entity entity) {
        if (professions == null) {
            return true;
        }

        if (entity instanceof VillagerEntity villager) {
            for (VillagerMatcher matcher : professions) {
                if (matcher.matchesVillager(villager)) {
                    return true;
                }
            }
        } else if (entity instanceof WanderingTraderEntity trader) {
            for (VillagerMatcher matcher : professions) {
                if (matcher.matchesWanderingTrader(trader)) {
                    return true;
                }
            }
        }
        return false;
    }
    // endregion

    public static OptiFineProperties parse(ResourceLoadContext context) throws IOException {
        Properties properties = new Properties();
        Optional<Resource> resource = context.getResource();
        if (resource.isPresent()) {
            properties.load(resource.get().getInputStream());
        }
        context.setProperties(properties);
        return new OptiFineProperties(context);
    }

    private static interface ContainerRemapper {
        void remapContainer(Properties packProps);
    }

    private static interface BlockMatcher {
        boolean matchesBlock(BlockPos pos);
    }

    private static interface EntityMatcher {
        boolean matchesEntity(Entity entity);
    }
}
