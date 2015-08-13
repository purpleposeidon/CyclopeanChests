package io.github.purpleposeidon.oldchests;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class EnderChestReplacement extends BlockEnderChest {
    public EnderChestReplacement() {
        YeOldeChesttes.chest_blocks.add(this);
        setBlockTextureName("old_ender_chest");
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z) {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @SideOnly(Side.CLIENT)
    IIcon front, side, top, bottom;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        front = reg.registerIcon(YeOldeChesttes.D + textureName + "_front");
        side = reg.registerIcon(YeOldeChesttes.D + textureName + "_side");
        top = reg.registerIcon(YeOldeChesttes.D + textureName + "_top");
        bottom = reg.registerIcon(YeOldeChesttes.D + textureName + "_bottom");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int md) {
        if (side == 0) return bottom;
        if (side == 1) return top;
        if (side == md) return front;
        return this.side;
    }

    @Override
    public int getRenderType() {
        return 0;
    }
}
