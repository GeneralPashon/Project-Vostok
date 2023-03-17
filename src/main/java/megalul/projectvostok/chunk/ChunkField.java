package megalul.projectvostok.chunk;

import megalul.projectvostok.block.BlockState;
import megalul.projectvostok.block.Block;

import static megalul.projectvostok.chunk.ChunkUtils.*;

public class ChunkField{

    private final Chunk chunkOf;

    private final short[] blocks;
    private final ChunkHeightMap heightMap;
    private volatile boolean dirty;

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

        if(oldID != block.type.id){
            dirty = true;

            if(!isOutOfBounds(x, z))
                updateHeight(x, y, z, block.type != Block.AIR);

            updateEdgesOfNeighborChunks(x, y, z, block);
        }
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


    private void updateEdgesOfNeighborChunks(int x, int y, int z, BlockState block){
        if(x == 0){
            Chunk neighbor = getNeighbor(-1, 0);
            if(neighbor != null)
                neighbor.getField().set(SIZE, y, z, block);
        }else if(x == SIZE_IDX){
            Chunk neighbor = getNeighbor(1, 0);
            if(neighbor != null)
                neighbor.getField().set(-1, y, z, block);
        }
        if(z == 0){
            Chunk neighbor = getNeighbor(0, -1);
            if(neighbor != null)
                neighbor.getField().set(x, y, SIZE, block);
        }else if(z == SIZE_IDX){
            Chunk neighbor = getNeighbor(0 , 1);
            if(neighbor != null)
                neighbor.getField().set(x, y, -1, block);
        }
    }

    private Chunk getNeighbor(int x, int z){
        return chunkOf.providerOf.getChunk(chunkOf.getPos().neighbor(x, z));
    }


    public ChunkHeightMap getHeightMap(){
        return heightMap;
    }

    public boolean isDirty(){
        return dirty;
    }

    public void built(){
        dirty = false;
    }

}
