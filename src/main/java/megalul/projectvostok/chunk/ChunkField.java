package megalul.projectvostok.chunk;

import megalul.projectvostok.block.BlockState;
import megalul.projectvostok.block.Block;

public class ChunkField extends ChunkUtils{

    private final Chunk chunkOf;

    private final short[] blocks;
    private final ChunkHeightMap heightMap;

    public ChunkField(Chunk chunkOf){
        this.chunkOf = chunkOf;

        heightMap = new ChunkHeightMap();
        blocks = new short[C_SIZE_3D];
    }


    public BlockState get(int x, int y, int z){
        return new BlockState(blocks[getIndex(x, y, z)]);
    }

    public void set(int x, int y, int z, BlockState block){
        byte oldID = BlockState.getIDFromState(blocks[getIndex(x, y, z)]);
        blocks[getIndex(x, y, z)] = block.getState();

        if(oldID != block.type.id)
            updateHeight(x, y, z, block.type != Block.AIR);

        updateEdgesOfNeighborChunks(chunkOf, x, y, z, block);
    }


    private void updateHeight(int x, int y, int z, boolean placed){
        int height = heightMap.getHeight(x, z);

        if(y == height && !placed)
            for(height--; getID(x, height, z) == Block.AIR.id && height > 0; )
                height--;
        else if(y > height && placed)
            height = y;

        heightMap.setHeight(x, z, height);
    }

    private int getID(int x, int y, int z){
        return BlockState.getIDFromState(blocks[getIndex(x, y, z)]);
    }


    private void updateEdgesOfNeighborChunks(Chunk chunk, int x, int y, int z, BlockState block){
        if(x == 0 && chunk.neighbors.get(0))
            chunk.providerOf.getChunk(chunk.getPos().neighbor(-1, 0)).getBlocks().set(SIZE, y, z, block);
        else if(x == SIZE - 1 && chunk.neighbors.get(1))
            chunk.providerOf.getChunk(chunk.getPos().neighbor(1, 0)).getBlocks().set(0, y, z, block);
        if(z == 0 && chunk.neighbors.get(2))
            chunk.providerOf.getChunk(chunk.getPos().neighbor(0, -1)).getBlocks().set(x, y, SIZE, block);
        else if(z == SIZE - 1 && chunk.neighbors.get(3))
            chunk.providerOf.getChunk(chunk.getPos().neighbor(0, 1)).getBlocks().set(x, y, 0, block);
    }


    public ChunkHeightMap getHeightMap(){
        return heightMap;
    }

}
