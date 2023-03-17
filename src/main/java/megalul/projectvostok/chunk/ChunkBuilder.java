package megalul.projectvostok.chunk;

import megalul.projectvostok.block.BlockState;

import java.util.ArrayList;
import java.util.List;

import static megalul.projectvostok.chunk.ChunkUtils.*;

public class ChunkBuilder{

    private static final List<Float> verticesList = new ArrayList<>();

    public static float[] build(Chunk chunk){
        for(int x = 0; x < SIZE; x++)
            for(int y = 0; y < HEIGHT; y++)
                for(int z = 0; z < SIZE; z++){
                    BlockState block = chunk.getField().get(x, y, z);
                    if(block.type.properties.isEmpty())
                        continue;

                    if(chunk.getField().get(x - 1, y, z).type.properties.isEmpty())
                        addNxFace(x, y, z);
                    if(chunk.getField().get(x + 1, y, z).type.properties.isEmpty())
                        addPxFace(x, y, z);
                    if(chunk.getField().get(x, y - 1, z).type.properties.isEmpty())
                        addNyFace(x, y, z);
                    if(chunk.getField().get(x, y + 1, z).type.properties.isEmpty())
                        addPyFace(x, y, z);
                    if(chunk.getField().get(x, y, z - 1).type.properties.isEmpty())
                        addNzFace(x, y, z);
                    if(chunk.getField().get(x, y, z + 1).type.properties.isEmpty())
                        addPzFace(x, y, z);
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
        addVertex(x  ,y+1,z  , 1,1,1,1, 0,0);
        addVertex(x+1,y+1,z  , 1,1,1,1, 1,0);
        addVertex(x+1,y+1,z+1, 1,1,1,1, 1,1);
        addVertex(x+1,y+1,z+1, 1,1,1,1, 1,1);
        addVertex(x  ,y+1,z+1, 1,1,1,1, 0,1);
        addVertex(x  ,y+1,z  , 1,1,1,1, 0,0);
    }

    private static void addNzFace(int x, int y, int z){
        addVertex(x  ,y  ,z  , 1,1,1,1, 0,0);
        addVertex(x+1,y  ,z  , 1,1,1,1, 1,0);
        addVertex(x+1,y+1,z  , 1,1,1,1, 1,1);
        addVertex(x+1,y+1,z  , 1,1,1,1, 1,1);
        addVertex(x  ,y+1,z  , 1,1,1,1, 0,1);
        addVertex(x  ,y  ,z  , 1,1,1,1, 0,0);
    }

    private static void addPzFace(int x, int y, int z){
        addVertex(x+1,y  ,z+1, 1,1,1,1, 0,0);
        addVertex(x  ,y  ,z+1, 1,1,1,1, 1,0);
        addVertex(x  ,y+1,z+1, 1,1,1,1, 1,1);
        addVertex(x  ,y+1,z+1, 1,1,1,1, 1,1);
        addVertex(x+1,y+1,z+1, 1,1,1,1, 0,1);
        addVertex(x+1,y  ,z+1, 1,1,1,1, 0,0);
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
