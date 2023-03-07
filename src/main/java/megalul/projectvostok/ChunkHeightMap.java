package megalul.projectvostok;

public class ChunkHeightMap extends ChunkUtils{

    private final short[] heights;

    public ChunkHeightMap(){
        heights = new short[SIZE_2D];
    }


    public int getHeight(int x, int z){
        if(isOutOfBounds(x, z))
            return 0;

        return heights[getIndex(x, z)];
    }

    public void setHeight(int x, int z, int height){
        if(isOutOfBounds(x, z))
            return;

        heights[getIndex(x, z)] = (short) height;
    }

}
