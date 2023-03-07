package megalul.projectvostok;

import glit.math.Maths;

import static megalul.projectvostok.ChunkSection.SIZE;

public class ChunkUtils{

    public static final int SIZE_3D = SIZE * SIZE * SIZE;
    public static final int SIZE_2D = SIZE * SIZE;

    public static final BlockState AIR = new BlockState(BlockType.AIR);


    public static int getIndex(int x, int y, int z){
        return x + y * SIZE + z * SIZE_2D;
    }

    public static int getIndex(int x, int y){
        return x + y * SIZE;
    }

    public static int getIndex(int y){
        return Maths.round((float) y / Chunk.HEIGHT);
    }


    public static boolean isOutOfBounds(int x, int y, int z){
        return x >= SIZE || y >= SIZE || z >= SIZE || x < 0 || y < 0 || z < 0;
    }

    public static boolean isOutOfBounds(int x, int y){
        return x >= SIZE || y >= SIZE || x < 0 || y < 0;
    }

    public static boolean isOutOfBounds(int y){
        return y >= Chunk.HEIGHT || y < 0;
    }


    public static int getLocalY(int y){
        return y & (SIZE - 1);
    }

}
