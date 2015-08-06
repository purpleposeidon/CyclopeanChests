package io.github.purpleposeidon.oldchests;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.ExistingSubstitutionException;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod(modid = YeOldeChesttes.MODID)
public class YeOldeChesttes {
    static final String MODID = "oldchests";
    static final String D = MODID + ":";
    List<Block> chest_blocks = Arrays.asList(Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest);
    ArrayList<Block> replacements = new ArrayList<Block>();

    @Mod.EventHandler
    public void initAll(FMLPreInitializationEvent event) throws ExistingSubstitutionException {
        replace("minecraft:chest", new BlockChestOld(0), "old_chest");
        replace("minecraft:trapped_chest", new BlockChestOld(1), "old_trapped_chest");
        replace("minecraft:ender_chest", new BlockEnderChestOld(), "old_ender_chest");
        MinecraftForge.EVENT_BUS.register(this);
    }

    void replace(String origName, Block replacement, String name) throws ExistingSubstitutionException {
        Block original = Block.getBlockFromName(origName);
        assert original != null;
        GameRegistry.addSubstitutionAlias(origName, GameRegistry.Type.BLOCK, replacement);
        GameRegistry.addSubstitutionAlias(origName, GameRegistry.Type.ITEM, new ItemBlock(replacement));
        replacement.setBlockName(name);
        replacement.setBlockTextureName(name);
        replacement.setHardness(original.getBlockHardness(null, 0, 0, 0));
        replacement.setStepSound(original.stepSound);
        replacement.setLightLevel(original.getLightValue() / 15F);
        replacement.setResistance(original.getExplosionResistance(null) * 5F / 3F);
        //GameRegistry.registerBlock(replacement, origName.replace("minecraft", MODID));
        replacements.add(replacement);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void setupIcons(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) return;
        for (Block b : replacements) {
            b.registerBlockIcons(event.map);
        }
    }

    static int md2side(int md) {
        return md;
    }

    class BlockChestOld extends BlockChest {
        protected BlockChestOld(int chestKind) {
            super(chestKind);
        }

        @Override
        public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z) {
            setBlockBounds(0, 0, 0, 1, 1, 1);
        }

        IIcon front, side, top, front_left, front_right, back_left, back_right;

        @Override
        public void registerBlockIcons(IIconRegister reg) {
            super.registerBlockIcons(reg);
            front = reg.registerIcon(D + textureName + "_front");
            side = reg.registerIcon(D + textureName + "_side");
            top = reg.registerIcon(D + textureName + "_top");
            front_left = reg.registerIcon(D + textureName + "_front_left");
            front_right = reg.registerIcon(D + textureName + "_front_right");
            back_left = reg.registerIcon(D + textureName + "_back_left");
            back_right = reg.registerIcon(D + textureName + "_back_right");
        }


        @Override
        public IIcon getIcon(int side, int md) {
            if (side == 0 || side == 1) return top;
            int face = md2side(md);
            if (side == face) return front;
            return this.side;
        }

        @Override
        public IIcon getIcon(IBlockAccess w, int x, int y, int z, int side) {
            if (side == 0 || side == 1) return top;
            int md = w.getBlockMetadata(x, y, z);
            int face = md2side(md);
            if (side == face || side == (face ^ 1)) {
                ForgeDirection dir = ForgeDirection.getOrientation(side);
                boolean back = false;
                if (side != face) {
                    dir = dir.getOpposite();
                    back = true;
                }
                TileEntity te = w.getTileEntity(x, y, z);
                if (!(te instanceof TileEntityChest)) return back ? this.side : front;
                TileEntityChest chest = (TileEntityChest) te;
                chest.checkForAdjacentChests();
                if (dir == ForgeDirection.WEST) {
                    if (chest.adjacentChestZNeg != null) return back ? back_left : front_right;
                    if (chest.adjacentChestZPos != null) return back ? back_right : front_left;
                }
                if (dir == ForgeDirection.EAST) {
                    if (chest.adjacentChestZNeg != null) return back ? back_right : front_left;
                    if (chest.adjacentChestZPos != null) return back ? back_left : front_right;
                }
                if (dir == ForgeDirection.NORTH) {
                    if (chest.adjacentChestXNeg != null) return back ? back_right : front_left;
                    if (chest.adjacentChestXPos != null) return back ? back_left : front_right;
                }
                if (dir == ForgeDirection.SOUTH) {
                    if (chest.adjacentChestXNeg != null) return back ? back_left : front_right;
                    if (chest.adjacentChestXPos != null) return back ? back_right : front_left;
                }

                return back ? this.side : front;
            }
            return this.side;
        }

        @Override
        public int getRenderType() {
            return 0;
        }
    }

    class BlockEnderChestOld extends BlockEnderChest {
        @Override
        public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z) {
            setBlockBounds(0, 0, 0, 1, 1, 1);
        }

        IIcon front, side, top, bottom;

        @Override
        public void registerBlockIcons(IIconRegister reg) {
            super.registerBlockIcons(reg);
            front = reg.registerIcon(D + textureName + "_front");
            side = reg.registerIcon(D + textureName + "_side");
            top = reg.registerIcon(D + textureName + "_top");
            bottom = reg.registerIcon(D + textureName + "_bottom");
        }

        @Override
        public IIcon getIcon(int side, int md) {
            if (side == 0) return bottom;
            if (side == 1) return top;
            int face = md2side(md);
            if (side == face) return front;
            return this.side;
        }

        @Override
        public int getRenderType() {
            return 0;
        }
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void initClient(FMLPreInitializationEvent event) {
        for (Block b : chest_blocks) {
            if (!(b instanceof BlockContainer)) continue;
            BlockContainer chest = (BlockContainer) b;
            TileEntity te = chest.createNewTileEntity(null, 0);
            if (te == null) continue;
            TileEntityRendererDispatcher.instance.mapSpecialRenderers.remove(te.getClass());
        }
        //final RenderChestBlock handler = new RenderChestBlock();
        //RenderingRegistry.registerBlockHandler(chestRenderId, handler);
    }

    @SideOnly(Side.CLIENT)
    public class RenderChestBlock implements ISimpleBlockRenderingHandler {
        @Override
        public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks rb) {
            rb.renderBlockAsItem(block, metadata, 1F);
            return; // It is necessary. Uhm.
        }

        @Override
        public boolean renderWorldBlock(IBlockAccess w, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
            return rb.renderStandardBlock(block, x, y, z);
        }

        @Override
        public boolean shouldRender3DInInventory(int modelId) {
            return true;
        }

        @Override
        public int getRenderId() {
            return 1; //chestRenderId;
        }
    }
}
