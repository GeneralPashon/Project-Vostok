package megalul.projectvostok.chunk;

import static megalul.projectvostok.chunk.ChunkUtils.*;

public class ChunkHeightMap{

    private final short[] heights;

    public ChunkHeightMap(){
        heights = new short[SIZE_2D];
    }


    public int getHeight(int x, int z){
        return heights[getIndex(x, z)];
    }

    public void setHeight(int x, int z, int height){
        heights[getIndex(x, z)] = (short) height;
    }

}
