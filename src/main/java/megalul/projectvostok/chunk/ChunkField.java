package megalul.projectvostok.chunk;

import megalul.projectvostok.block.BlockState;
import megalul.projectvostok.block.BlockType;

public class ChunkField extends ChunkUtils{

    private final short[] blocks;
    private final ChunkHeightMap heightMap;

    public ChunkField(){
        heightMap = new ChunkHeightMap();
        blocks = new short[C_SIZE_3D];
    }


    public BlockState get(int x, int y, int z){
        return new BlockState(blocks[getIndex(x, y, z)]);
    }

    public int getID(int x, int y, int z){
        return BlockState.getIDFromState(blocks[getIndex(x, y, z)]);
    }

    public void set(int x, int y, int z, BlockState block){
        byte oldID = BlockState.getIDFromState(blocks[getIndex(x, y, z)]);
        blocks[getIndex(x, y, z)] = block.getState();
        boolean isChanged = oldID != block.type.id;

        if(isChanged){
            int height = heightMap.getHeight(x, z);

            if(y == height && block.type == BlockType.AIR)
                for(height--; getID(x, height, z) == BlockType.AIR.id && height > 0; )
                    height--;
            else if(y > height && block.type != BlockType.AIR)
                height = y;

            heightMap.setHeight(x, z, height);
        }
    }


    public ChunkHeightMap getHeightMap(){
        return heightMap;
    }

}
