package megalul.projectvostok;

public class ChunkBlocks extends ChunkUtils{

    private final short[] blocks;

    public ChunkBlocks(){
        blocks = new short[SIZE_3D];
    }


    public BlockState get(int x, int y, int z){
        if(isOutOfBounds(x, z))
            return AIR;

        return new BlockState(blocks[getIndex(x, y, z)]);
    }

    public boolean set(int x, int y, int z, BlockState block){
        if(isOutOfBounds(x, z))
            return false;

        byte oldID = BlockState.getIDFromState(blocks[getIndex(x, y, z)]);
        blocks[getIndex(x, y, z)] = block.getState();
        return oldID != block.type.id;
    }


    public int getIDNow(int x, int y, int z){
        return BlockState.getIDFromState(blocks[getIndex(x, y, z)]);
    }

}
