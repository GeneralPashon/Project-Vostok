package megalul.projectvostok.chunk;

import megalul.projectvostok.block.BlockType;

import java.util.ArrayList;
import java.util.List;

public class ChunkBuilder{

    private static final List<Float> verticesList = new ArrayList<>();

    public static float[] build(Chunk chunk){
        for(int i = 0; i < ChunkUtils.SIZE_XZ; i++)
            for(int j = 0; j < ChunkUtils.HEIGHT; j++)
                for(int k = 0; k < ChunkUtils.SIZE_XZ; k++)
                    if(chunk.getBlocks().get(i, j, k).type != BlockType.AIR)
                        addFace(i, j, k);

        float[] array = new float[verticesList.size()];
        for(int i = 0; i < array.length; i++)
            array[i] = verticesList.get(i);
        verticesList.clear();

        return array;
    }

    private static void addFace(int x, int y, int z){
        addVertex(x  ,y+1,z  , 1,1,1,1, 0,0);
        addVertex(x+1,y+1,z  , 1,1,1,1, 1,0);
        addVertex(x+1,y+1,z+1, 1,1,1,1, 1,1);
        addVertex(x+1,y+1,z+1, 1,1,1,1, 1,1);
        addVertex(x  ,y+1,z+1, 1,1,1,1, 0,1);
        addVertex(x  ,y+1,z  , 1,1,1,1, 0,0);

        // 0 ,0,0 , 1,1,1,1, 0,0,
        // 16,0,0 , 1,1,1,1, 1,0,
        // 16,0,16, 1,1,1,1, 1,1,
        // 16,0,16, 1,1,1,1, 1,1,
        // 0 ,0,16, 1,1,1,1, 0,1,
        // 0 ,0,0 , 1,1,1,1, 0,0,
    }

    private static void addVertex(float x, float y, float z, float r, float g, float b, float a, float u, float v){
        verticesList.add(x);
        verticesList.add(y);
        verticesList.add(z);
        verticesList.add(r);
        verticesList.add(g);
        verticesList.add(b);
        verticesList.add(a);
        verticesList.add(u);
        verticesList.add(v);
    }

}
