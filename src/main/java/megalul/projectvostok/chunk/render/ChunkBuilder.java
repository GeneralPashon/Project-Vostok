package megalul.projectvostok.chunk.render;

import megalul.projectvostok.block.BlockState;
import megalul.projectvostok.chunk.Chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static megalul.projectvostok.chunk.ChunkUtils.*;

public class ChunkBuilder{

    private static final List<Float> verticesList = new ArrayList<>();
    private static final byte[] masks = new byte[VOLUME];
    private static int vertices;

    public static float[] build(Chunk chunk){
        vertices = 0;

        for(int x = 0; x < SIZE; x++)
            for(int z = 0; z < SIZE; z++)
                for(int y = chunk.getDepth(x, z); y <= chunk.getHeight(x, z); y++){
                    BlockState block = chunk.getBlock(x, y, z);
                    if(block.getProp().isEmpty())
                        continue;

                    if(block.getProp().isSolid()){
                        byte mask = 0;

                        if(chunk.getBlock(x - 1, y, z).getProp().isEmpty()) mask |= 1;
                        if(chunk.getBlock(x + 1, y, z).getProp().isEmpty()) mask |= 2;
                        if(chunk.getBlock(x, y - 1, z).getProp().isEmpty()) mask |= 4;
                        if(chunk.getBlock(x, y + 1, z).getProp().isEmpty()) mask |= 8;
                        if(chunk.getBlock(x, y, z - 1).getProp().isEmpty()) mask |= 16;
                        if(chunk.getBlock(x, y, z + 1).getProp().isEmpty()) mask |= 32;

                        masks[getIndex(x, y, z)] = mask;
                    }
                }

        for(int i = 0; i < masks.length; i++){
            int x = i % SIZE;
            int z = (i - x) / SIZE % SIZE;
            int y = (i - x - z * SIZE) / AREA;
            byte mask = masks[i];

            if((mask      & 1) == 1) addNxFace(x, y, z);
            if((mask >> 1 & 1) == 1) addPxFace(x, y, z);
            if((mask >> 2 & 1) == 1) addNyFace(x, y, z);
            if((mask >> 3 & 1) == 1) addPyFace(x, y, z);
            if((mask >> 4 & 1) == 1) addNzFace(x, y, z);
            if((mask >> 5 & 1) == 1) addPzFace(x, y, z);
        }

        float[] array = new float[verticesList.size()];
        for(int i = 0; i < array.length; i++)
            array[i] = verticesList.get(i);

        verticesList.clear();
        Arrays.fill(masks, (byte) 0);

        System.out.println("Faces: " + (vertices / 6));

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

        vertices++;
    }

}
