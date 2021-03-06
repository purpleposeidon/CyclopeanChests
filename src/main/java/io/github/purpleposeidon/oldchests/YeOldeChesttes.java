package io.github.purpleposeidon.oldchests;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.purpleposeidon.oldchests.asm.OldChestLoadingPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

@Mod(modid = YeOldeChesttes.MODID, name = "Cyclopean Chests", version = "1.0")
public class YeOldeChesttes {
    public static final String MODID = "oldchests";
    public static final String TEXTURE = MODID + ":"; // TODO: Christmas textures

    public static final ArrayList<Block> chest_blocks = new ArrayList<Block>();

    {
        if (!OldChestLoadingPlugin.loaded_properly) {
            throw new RuntimeException("Coremod didn't load");
        }
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void initClient(FMLPreInitializationEvent event) {
        for (Block b : chest_blocks) {
            BlockContainer chest = (BlockContainer) b;
            TileEntity te = chest.createNewTileEntity(null, 0);
            if (te == null) continue;
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.remove(te.getClass());
        }
        // TODO: Needs a custom item renderer. Whatever. 1.8'll be here eventually.
    }
}
