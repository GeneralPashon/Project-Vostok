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
    public static final int[] AO_INDICES = new int[]{ 3, 0, 1, 1, 2, 3 };
    public static final byte[] AO = calcAO();


    private static final List<Float> verticesList = new ArrayList<>();
    private static final byte[] masks = new byte[C_VOLUME];

    private static int vertexIndex;
    private static Color v_color = new Color();
    private static float[] v_ao = new float[4];

    public static float[] build(Chunk chunk){
        vertexIndex = 0;

        for(int x = 0; x < C_SIZE; x++)
            for(int z = 0; z < C_SIZE; z++){

                final int bx = x - 1; // Block X
                final int bz = z - 1; // Block Z

                final int hx = Maths.clamp(x, 0, SIZE_IDX);
                final int hz = Maths.clamp(z, 0, SIZE_IDX);

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

                        masks[getIndexC(bx, y, bz)] = mask;
                    }
                }
            }

        /*for(int i = 0; i < masks.length; i++){
            final int x = i % C_SIZE;
            if(x == 0 || x == C_SIZE_IDX)
                continue;
            final int z = (i - x) / C_SIZE % C_SIZE;
            if(z == 0  || z == C_SIZE_IDX)
                continue;
            final int y = (i - x - z * C_SIZE) / C_AREA;
            final byte mask = masks[i];

            final int bx = x - 1;
            final int bz = z - 1;

            if((mask      & 1) == 1){
                setAO(AO[
                        (((masks[getIndexC(x, y, z)] << 0 & 1))     ) |
                        (((masks[getIndexC(x, y, z)] << 0 & 1)) << 1) |
                        (((masks[getIndexC(x, y, z)] << 0 & 1)) << 2) |
                        (((masks[getIndexC(x, y, z)] << 0 & 1)) << 3)
                ]);
                addNxFace(bx, y, bz);
            }
            if((mask >> 1 & 1) == 1){
                setAO(AO[
                        (((masks[getIndexC(x, y, z)] << 0 & 1))     ) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 1) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 2) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 3)
                        ]);
                addPxFace(bx, y, bz);
            }
            if((mask >> 2 & 1) == 1){
                setAO(AO[
                        (((masks[getIndexC(x, y, z)] << 0 & 1))     ) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 1) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 2) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 3)
                        ]);
                addNyFace(bx, y, bz);
            }
            if((mask >> 3 & 1) == 1){
                setAO(AO[
                        (((masks[getIndexC(x, y, z)] << 0 & 1))     ) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 1) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 2) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 3)
                        ]);
                addPyFace(bx, y, bz);
            }
            if((mask >> 4 & 1) == 1){
                setAO(AO[
                        (((masks[getIndexC(x, y, z)] << 0 & 1))     ) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 1) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 2) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 3)
                        ]);
                addNzFace(bx, y, bz);
            }
            if((mask >> 5 & 1) == 1){
                setAO(AO[
                        (((masks[getIndexC(x, y, z)] << 0 & 1))     ) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 1) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 2) |
                                (((masks[getIndexC(x, y, z)] << 0 & 1)) << 3)
                        ]);
                addPzFace(bx, y, bz);
            }
        }*/

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

    private static byte[] calcAO(){
        byte[] ao = new byte[8 * 8];

        for(int i = 0; i < ao.length; i++){
            int v1 = i % 2;
        }

        return ao;
    }


    private static void addNxFace(int x, int y, int z){
        addVertex(x  , y+1, z+1, 1, 1);
        addVertex(x  , y  , z+1, 0, 1);
        addVertex(x  , y  , z  , 0, 0);
        addVertex(x  , y  , z  , 0, 0);
        addVertex(x  , y+1, z  , 1, 0);
        addVertex(x  , y+1, z+1, 1, 1);
    }

    private static void addPxFace(int x, int y, int z){
        addVertex(x+1, y+1, z  , 1, 1);
        addVertex(x+1, y  , z  , 0, 1);
        addVertex(x+1, y  , z+1, 0, 0);
        addVertex(x+1, y  , z+1, 0, 0);
        addVertex(x+1, y+1, z+1, 1, 0);
        addVertex(x+1, y+1, z  , 1, 1);
    }

    private static void addNyFace(int x, int y, int z){
        addVertex(x+1, y  , z  , 1, 1);
        addVertex(x  , y  , z  , 0, 1);
        addVertex(x  , y  , z+1, 0, 0);
        addVertex(x  , y  , z+1, 0, 0);
        addVertex(x+1, y  , z+1, 1, 0);
        addVertex(x+1, y  , z  , 1, 1);
    }

    private static void addPyFace(int x, int y, int z){
        addVertex(x  , y+1, z  , 0, 0);
        addVertex(x+1, y+1, z  , 1, 0);
        addVertex(x+1, y+1, z+1, 1, 1);
        addVertex(x+1, y+1, z+1, 1, 1);
        addVertex(x  , y+1, z+1, 0, 1);
        addVertex(x  , y+1, z  , 0, 0);
    }

    private static void addNzFace(int x, int y, int z){
        addVertex(x  , y   ,z  , 0, 0);
        addVertex(x+1, y   ,z  , 1, 0);
        addVertex(x+1, y+1 ,z  , 1, 1);
        addVertex(x+1, y+1 ,z  , 1, 1);
        addVertex(x  , y+1 ,z  , 0, 1);
        addVertex(x  , y   ,z  , 0, 0);
    }

    private static void addPzFace(int x, int y, int z){
        addVertex(x+1, y  , z+1, 0, 0);
        addVertex(x  , y  , z+1, 1, 0);
        addVertex(x  , y+1, z+1, 1, 1);
        addVertex(x  , y+1, z+1, 1, 1);
        addVertex(x+1, y+1, z+1, 0, 1);
        addVertex(x+1, y  , z+1, 0, 0);
    }


    private static void addVertex(float x, float y, float z, float u, float v){
        float ao = v_ao[AO_INDICES[vertexIndex % 6]];

        verticesList.add(x);
        verticesList.add(y);
        verticesList.add(z);
        verticesList.add(v_color.r() * ao);
        verticesList.add(v_color.g() * ao);
        verticesList.add(v_color.b() * ao);
        verticesList.add(v_color.a());
        verticesList.add(u);
        verticesList.add(v);

        vertexIndex++;
    }

}
