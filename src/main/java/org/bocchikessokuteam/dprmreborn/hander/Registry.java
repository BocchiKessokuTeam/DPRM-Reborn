package org.bocchikessokuteam.dprmreborn.hander;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;
import org.bocchikessokuteam.dprmreborn.container.*;

public class Registry {
    public static final DeferredRegister<MenuType<?>> CONTAINERS_TYPE = DeferredRegister.create(ForgeRegistries.CONTAINERS, DPRMReborn.MODID);

    //Container
    public static RegistryObject<MenuType<CraftingTableContainer>> craftingContainer = CONTAINERS_TYPE.register("crafting_container", () -> IForgeContainerType.create(CraftingTableContainer::new));
    public static RegistryObject<MenuType<FurnaceContainer>> furnaceContainer = CONTAINERS_TYPE.register("furnace_container",() -> IForgeContainerType.create(FurnaceContainer::new));
    public static RegistryObject<MenuType<StonecuttingContainer>> stonecuttingContainer = CONTAINERS_TYPE.register("stonecutting_container",() -> IForgeContainerType.create(StonecuttingContainer::new));
    public static RegistryObject<MenuType<SmithingContainer>> smithingContainer = CONTAINERS_TYPE.register("smithing_container",() -> IForgeContainerType.create(SmithingContainer::new));
    public static RegistryObject<MenuType<RecipeListContainer>> recipeListContainer = CONTAINERS_TYPE.register("recipe_list_container",() -> IForgeContainerType.create(RecipeListContainer::new));

}
