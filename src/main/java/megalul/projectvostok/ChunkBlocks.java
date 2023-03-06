package megalul.projectvostok;

public class ChunkBlocks extends ChunkDataContainer{

    private final ChunkBlock[] blocks;

    public ChunkBlocks(){
        blocks = new ChunkBlock[SIZE_3D];
        for(int i = 0; i < blocks.length; i++)
            blocks[i] = new ChunkBlock();
    }

    public ChunkBlock get(int x, int y, int z){
        if(isOutOfBounds(x, y, z))
            return null;

        return blocks[getIndex(x, y, z)];
    }

}
