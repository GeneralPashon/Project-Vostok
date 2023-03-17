package megalul.projectvostok.chunk.data;

import static megalul.projectvostok.chunk.ChunkUtils.*;

public class ChunkHeightMap{

    private final short[] heights;
    private final short[] depths;

    public ChunkHeightMap(){
        heights = new short[AREA];
        depths = new short[AREA];
    }


    public int getHeight(int x, int z){
        return heights[getIndex(x, z)];
    }

    public void setHeight(int x, int z, int height){
        heights[getIndex(x, z)] = (short) height;
    }


    public int getDepth(int x, int z){
        return depths[getIndex(x, z)];
    }

    public void setDepth(int x, int z, int height){
        depths[getIndex(x, z)] = (short) height;
    }

}
