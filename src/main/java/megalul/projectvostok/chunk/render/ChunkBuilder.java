package megalul.projectvostok.chunk.render;

import glit.graphics.util.color.Color;
import glit.math.Maths;
import megalul.projectvostok.block.BlockState;
import megalul.projectvostok.chunk.Chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static megalul.projectvostok.chunk.ChunkUtils.*;

public class ChunkBuilder{

    public static final float AO_BRIGHTNESS = 0.5F;


    private static final List<Float> verticesList = new ArrayList<>();
    private static final byte[] masks = new byte[C_VOLUME];

    private static Color v_color = new Color();
    private static float[] v_ao = new float[4];

    public static float[] build(Chunk chunk){
        for(int x = 0; x < C_SIZE; x++)
            for(int z = 0; z < C_SIZE; z++){

                final int bx = x - 1; // Block X
                final int bz = z - 1; // Block Z

                final int hx = Maths.clamp(x, 0, SIZE_IDX);
                final int hz = Maths.clamp(x, 0, SIZE_IDX);

                final int maxHeight = chunk.getHeight(hx, hz) + 1;

                for(int y = chunk.getDepth(hx, hz); y < maxHeight; y++){
                    final BlockState block = chunk.getBlock(bx, y, bz);
                    if(block.getProp().isEmpty())
                        continue;

                    if(block.getProp().isSolid()){
                        byte mask = 0;

                        if(chunk.getBlock(bx - 1, y, bz).getProp().isEmpty()) mask |= 1;
                        if(chunk.getBlock(bx + 1, y, bz).getProp().isEmpty()) mask |= 2;
                        if(chunk.getBlock(bx, y - 1, bz).getProp().isEmpty()) mask |= 4;
                        if(chunk.getBlock(bx, y + 1, bz).getProp().isEmpty()) mask |= 8;
                        if(chunk.getBlock(bx, y, bz - 1).getProp().isEmpty()) mask |= 16;
                        if(chunk.getBlock(bx, y, bz + 1).getProp().isEmpty()) mask |= 32;

                        masks[getIndex(bx, y, bz)] = mask;
                    }
                }
            }

        for(int i = 0; i < masks.length; i++){
            final int x = i % C_SIZE;
            if(x == 0 || x == C_SIZE_IDX)
                continue;
            final int z = (i - x) / C_SIZE % C_SIZE;
            if(z == 0  || z == C_SIZE_IDX)
                continue;
            final int y = (i - x - z * C_SIZE) / C_AREA;
            final byte mask = masks[i];



            // setAO(getAO(
            //
            // ));

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

        return array;
    }


    private static void setAO(byte ao){
        for(int i = 0; i < 4; i++)
            v_ao[i] = (ao >> i & 1) == 1 ? AO_BRIGHTNESS : 1;
    }


    private static void addNxFace(int x, int y, int z){
        addVertex(x  ,y+1,z+1, v_color.r() * v_ao[0], v_color.g() * v_ao[0], v_color.b() * v_ao[0], v_color.a(), 1,1);
        addVertex(x  ,y  ,z+1, v_color.r() * v_ao[0], v_color.g() * v_ao[0], v_color.b() * v_ao[0], v_color.a(), 0,1);
        addVertex(x  ,y  ,z  , v_color.r() * v_ao[0], v_color.g() * v_ao[0], v_color.b() * v_ao[0], v_color.a(), 0,0);
        addVertex(x  ,y  ,z  , v_color.r() * v_ao[0], v_color.g() * v_ao[0], v_color.b() * v_ao[0], v_color.a(), 0,0);
        addVertex(x  ,y+1,z  , v_color.r() * v_ao[0], v_color.g() * v_ao[0], v_color.b() * v_ao[0], v_color.a(), 1,0);
        addVertex(x  ,y+1,z+1, v_color.r() * v_ao[0], v_color.g() * v_ao[0], v_color.b() * v_ao[0], v_color.a(), 1,1);
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
