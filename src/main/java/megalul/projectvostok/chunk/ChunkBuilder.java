package megalul.projectvostok.chunk;

import megalul.projectvostok.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ChunkBuilder{

    private static final List<Float> verticesList = new ArrayList<>();

    public static float[] build(Chunk chunk){
        for(int x = 0; x < ChunkUtils.SIZE; x++)
            for(int y = 0; y < ChunkUtils.HEIGHT; y++)
                for(int z = 0; z < ChunkUtils.SIZE; z++){
                    BlockState block = chunk.getBlocks().get(x, y, z);
                    if(!block.type.properties.isSolid())
                        continue;

                    addNxFace(x, y, z);
                    addPxFace(x, y, z);
                    addNyFace(x, y, z);
                    addPyFace(x, y, z);
                }

        float[] array = new float[verticesList.size()];
        for(int i = 0; i < array.length; i++)
            array[i] = verticesList.get(i);
        verticesList.clear();

        return array;
    }


    private static void addNxFace(int x, int y, int z){
        addVertex(x  ,y+1,z+1, 1,1,1,1, 1,1);
        addVertex(x  ,y  ,z+1, 1,1,1,1, 0,1);
        addVertex(x  ,y  ,z  , 1,1,1,1, 0,0);
        addVertex(x  ,y  ,z  , 1,1,1,1, 0,0);
        addVertex(x  ,y+1,z  , 1,1,1,1, 1,0);
        addVertex(x  ,y+1,z+1, 1,1,1,1, 1,1);
    }

    private static void addPxFace(int x, int y, int z){
        addVertex(x+1,y+1,z  , 1,1,1,1, 1,1);
        addVertex(x+1,y  ,z  , 1,1,1,1, 0,1);
        addVertex(x+1,y  ,z+1, 1,1,1,1, 0,0);
        addVertex(x+1,y  ,z+1, 1,1,1,1, 0,0);
        addVertex(x+1,y+1,z+1, 1,1,1,1, 1,0);
        addVertex(x+1,y+1,z  , 1,1,1,1, 1,1);
    }

    private static void addNyFace(int x, int y, int z){
        addVertex(x+1,y  ,z  , 1,1,1,1, 1,1);
        addVertex(x  ,y  ,z  , 1,1,1,1, 0,1);
        addVertex(x  ,y  ,z+1, 1,1,1,1, 0,0);
        addVertex(x  ,y  ,z+1, 1,1,1,1, 0,0);
        addVertex(x+1,y  ,z+1, 1,1,1,1, 1,0);
        addVertex(x+1,y  ,z  , 1,1,1,1, 1,1);
    }

    private static void addPyFace(int x, int y, int z){
        addVertex(x  ,y+1,z  , 1,1,1,1, 0,0); // 0 ,0,0 , 1,1,1,1, 0,0,
        addVertex(x+1,y+1,z  , 1,1,1,1, 1,0); // 16,0,0 , 1,1,1,1, 1,0,
        addVertex(x+1,y+1,z+1, 1,1,1,1, 1,1); // 16,0,16, 1,1,1,1, 1,1,
        addVertex(x+1,y+1,z+1, 1,1,1,1, 1,1); // 16,0,16, 1,1,1,1, 1,1,
        addVertex(x  ,y+1,z+1, 1,1,1,1, 0,1); // 0 ,0,16, 1,1,1,1, 0,1,
        addVertex(x  ,y+1,z  , 1,1,1,1, 0,0); // 0 ,0,0 , 1,1,1,1, 0,0,
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
