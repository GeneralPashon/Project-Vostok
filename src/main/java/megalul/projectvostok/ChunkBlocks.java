package megalul.projectvostok;

public class ChunkBlocks extends ChunkDataContainer{

    private final short[] blocks;

    public ChunkBlocks(){
        blocks = new short[SIZE_3D];
    }


    public short get(int x, int y, int z){
        if(isOutOfBounds(x, y, z))
            return 0;

        return blocks[getIndex(x, y, z)];
    }

    public void set(int x, int y, int z, short block){
        if(isOutOfBounds(x, y, z))
            return;

        blocks[getIndex(x, y, z)] = block;
    }


    public BlockState getBlock(int x, int y, int z){
        return new BlockState((byte) ((get(x, y, z) >> 8) & 0xFF));
    }

}
