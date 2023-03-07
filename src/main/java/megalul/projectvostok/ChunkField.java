package megalul.projectvostok;

public class ChunkField extends ChunkUtils{

    private final short[] blocks;
    private final ChunkHeightMap heightMap;

    public ChunkField(){
        heightMap = new ChunkHeightMap();
        blocks = new short[SIZE_3D];
    }


    public BlockState get(int x, int y, int z){
        if(isOutOfBounds(x, y, z))
            return AIR;

        return new BlockState(blocks[getIndex(x, y, z)]);
    }

    public void set(int x, int y, int z, BlockState block){
        if(isOutOfBounds(x, y, z))
            return;

        byte oldID = BlockState.getIDFromState(blocks[getIndex(x, y, z)]);
        blocks[getIndex(x, y, z)] = block.getState();
        boolean isChanged = oldID != block.type.id;

        if(isChanged){
            int height = heightMap.getHeight(x, z);

            if(y == height && block.type == BlockType.AIR)
                for(height--; getIDNow(x, height, z) == BlockType.AIR.id && height > 0; )
                    height--;
            else if(y > height && block.type != BlockType.AIR)
                height = y;

            heightMap.setHeight(x, z, height);
        }
    }


    public int getIDNow(int x, int y, int z){
        return BlockState.getIDFromState(blocks[getIndex(x, y, z)]);
    }

    public void setIDNow(int x, int y, int z, int id){
       blocks[getIndex(x, y, z)] = (short) id;
    }


    public ChunkHeightMap getHeightMap(){
        return heightMap;
    }

}
