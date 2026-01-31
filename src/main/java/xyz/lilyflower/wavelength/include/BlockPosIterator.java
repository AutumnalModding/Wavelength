package xyz.lilyflower.wavelength.include;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import javax.annotation.Nonnull;

public class BlockPosIterator extends AbstractIterator<BlockPos> implements Iterable<BlockPos> {
    private final int startX;
    private final int startY;
    private final int startZ;
    private final int endX;
    private final int endY;
    private final int endZ;
    private boolean first = true;
    private int lastPosX;
    private int lastPosY;
    private int lastPosZ;

    public BlockPosIterator(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    @Nonnull
    public Iterator<BlockPos> iterator() {
        return this;
    }


    @Override
    protected BlockPos computeNext() {
        if (this.first) {
            this.first = false;
            this.lastPosX = startX;
            this.lastPosY = startY;
            this.lastPosZ = startZ;
            return new BlockPos(startX, startY, startZ);
        } else if (this.lastPosX == endX && this.lastPosY == endY
                && this.lastPosZ == endZ) {
            return this.endOfData();
        } else {
            if (this.lastPosX < endX) {
                ++this.lastPosX;
            } else if (this.lastPosY < endY) {
                this.lastPosX = startX;
                ++this.lastPosY;
            } else if (this.lastPosZ < endZ) {
                this.lastPosX = startX;
                this.lastPosY = startY;
                ++this.lastPosZ;
            }

            return new BlockPos(this.lastPosX, this.lastPosY, this.lastPosZ);
        }
    }
}
