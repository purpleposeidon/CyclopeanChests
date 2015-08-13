package io.github.purpleposeidon.oldchests;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class ChestReplacement extends BlockChest {
    public ChestReplacement(int chestKind) {
        super(chestKind);
        if (chestKind == 0) {
            setBlockTextureName("old_chest");
        } else {
            setBlockTextureName("old_trapped_chest");
        }

        YeOldeChesttes.chest_blocks.add(this);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z) {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @SideOnly(Side.CLIENT)
    IIcon front, side, top, front_left, front_right, back_left, back_right;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        front = reg.registerIcon(YeOldeChesttes.TEXTURE + textureName + "_front");
        side = reg.registerIcon(YeOldeChesttes.TEXTURE + textureName + "_side");
        top = reg.registerIcon(YeOldeChesttes.TEXTURE + textureName + "_top");
        front_left = reg.registerIcon(YeOldeChesttes.TEXTURE + textureName + "_front_left");
        front_right = reg.registerIcon(YeOldeChesttes.TEXTURE + textureName + "_front_right");
        back_left = reg.registerIcon(YeOldeChesttes.TEXTURE + textureName + "_back_left");
        back_right = reg.registerIcon(YeOldeChesttes.TEXTURE + textureName + "_back_right");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int md) {
        if (side == 0 || side == 1) return top;
        if (side == md) return front;
        return this.side;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess w, int x, int y, int z, int side) {
        // TODO: Might want to get rid of the seam on the top & bottom of joined chests

        if (side == 0 || side == 1) return top;
        int face = w.getBlockMetadata(x, y, z);
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

    //... So, this is stupid. java.lang.NoSuchMethodError: io.github.purpleposeidon.oldchests.ChestReplacement.c(F)Lnet/minecraft/block/Block;

    public Block c(float hardness) {
        return setHardness(hardness);
    }

    public Block a(SoundType sound) {
        return setStepSound(sound);
    }

    public Block c(String name) {
        return setBlockName(name);
    }

    public Block b(float resistance) {
        return setResistance(resistance);
    }

    public Block a(float lightLevel) {
        return setLightLevel(lightLevel);
    }
}
