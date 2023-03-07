package megalul.projectvostok;

public class ChunkUtils{

    public static final int HEIGHT = 256;
    public static final int SIZE_XZ = 16;
    public static final int SIZE_2D = SIZE_XZ * SIZE_XZ;
    public static final int SIZE_3D = SIZE_2D * HEIGHT;

    public static final BlockState AIR = new BlockState(BlockType.AIR);


    public static int getIndex(int x, int y, int z){
        return x + (z + y * SIZE_XZ) * SIZE_XZ;
    }

    public static int getIndex(int x, int z){
        return x + z * SIZE_XZ;
    }


    public static boolean isOutOfBounds(int x, int y, int z){
        return x >= SIZE_XZ || y >= HEIGHT || z >= SIZE_XZ || x < 0 || y < 0 || z < 0;
    }

    public static boolean isOutOfBounds(int x, int z){
        return x >= SIZE_XZ || z >= SIZE_XZ || x < 0 || z < 0;
    }

}
