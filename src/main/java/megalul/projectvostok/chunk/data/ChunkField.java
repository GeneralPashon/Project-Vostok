package megalul.projectvostok.chunk.data;

import megalul.projectvostok.Main;
import megalul.projectvostok.block.BlockState;
import megalul.projectvostok.block.blocks.Block;
import megalul.projectvostok.chunk.Chunk;

import static megalul.projectvostok.chunk.ChunkUtils.*;

public class ChunkField{

    private final Chunk chunkOf;

    private final short[] blocks;
    private final ChunkHeightMap heightMap;
    private volatile boolean dirty;

    public ChunkField(Chunk chunkOf){
        this.chunkOf = chunkOf;

        heightMap = new ChunkHeightMap();
        blocks = new short[C_VOLUME];
    }


    public BlockState get(int x, int y, int z){
        return new BlockState(blocks[getIndexC(x, y, z)]);
    }

    public void set(int x, int y, int z, BlockState block){
        byte oldID = BlockState.getIDFromState(blocks[getIndexC(x, y, z)]);
        blocks[getIndexC(x, y, z)] = block.getState();

        if(oldID != block.getID() && !isOutOfBounds(x, z)){
            dirty = true;

            updateEdgesOfNeighborChunks(x, y, z, block);

            updateHeight(x, y, z, !block.getProp().isEmpty());
            if(Main.UPDATE_DEPTH_MAP)
                updateDepth(x, y, z, !block.getProp().isEmpty());
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

    private void updateDepth(int x, int y, int z, boolean placed){
        int depth = heightMap.getDepth(x, z);

        if(y == depth && !placed)
            for(depth++; getID(x, depth, z) == Block.AIR.id && depth < HEIGHT_IDX; )
                depth++;
        else if(y < depth && placed)
            depth = y;

        heightMap.setDepth(x, z, depth);
    }

    private int getID(int x, int y, int z){
        return BlockState.getIDFromState(blocks[getIndexC(x, y, z)]);
    }


    private void updateEdgesOfNeighborChunks(int x, int y, int z, BlockState block){
        if(x == 0){
            Chunk neighbor = getNeighbor(-1, 0);
            if(neighbor != null){
                neighbor.setBlock(SIZE, y, z, block);
                chunkOf.getProvider().rebuildChunk(neighbor);
            }
        }else if(x == SIZE_IDX){
            Chunk neighbor = getNeighbor(1, 0);
            if(neighbor != null){
                neighbor.setBlock(-1, y, z, block);
                chunkOf.getProvider().rebuildChunk(neighbor);
            }
        }
        if(z == 0){
            Chunk neighbor = getNeighbor(0, -1);
            if(neighbor != null){
                neighbor.setBlock(x, y, SIZE, block);
                chunkOf.getProvider().rebuildChunk(neighbor);
            }
        }else if(z == SIZE_IDX){
            Chunk neighbor = getNeighbor(0 , 1);
            if(neighbor != null){
                neighbor.setBlock(x, y, -1, block);
                chunkOf.getProvider().rebuildChunk(neighbor);
            }
        }
    }

    private Chunk getNeighbor(int x, int z){
        return chunkOf.getProvider().getChunk(chunkOf.getPos().getNeighbor(x, z));
    }


    public ChunkHeightMap getHeightMap(){
        return heightMap;
    }

    public boolean isDirty(){
        return dirty;
    }

    public void onMeshUpdate(){
        dirty = false;
    }

}
