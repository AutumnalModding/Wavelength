package xyz.lilyflower.wavelength.test;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import xyz.lilyflower.wavelength.util.BootlegExplosionHandler;

public class BlockExplosionTest extends Block {
    public BlockExplosionTest() {
        super(Material.rock);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (world instanceof WorldServer server) {
            BootlegExplosionHandler.sphere(server, new BlockPos(x, y, z), 25, 130);
            return true;
        }

        return false;
    }
}
