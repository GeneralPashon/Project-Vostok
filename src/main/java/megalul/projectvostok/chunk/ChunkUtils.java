package megalul.projectvostok.chunk;

public class ChunkUtils{

    public static int SIZE = 16;
    public static int HEIGHT = 256;
    public static int SIZE_2D = SIZE * SIZE;

    // C = data Container
    public static final int C_HEIGHT = HEIGHT + 2;
    public static final int C_SIZE = SIZE + 2;
    public static final int C_SIZE_2D = C_SIZE * C_SIZE;
    public static final int C_SIZE_3D = C_SIZE_2D * C_HEIGHT;


    public static int getIndex(int x, int y, int z){
        return (x + 1) + ((z + 1) + (y + 1) * C_SIZE) * C_SIZE;
    }

    public static int getIndex(int x, int z){
        return x + z * SIZE;
    }


    public static boolean isOutOfBounds(int x, int y, int z){
        return x >= SIZE || y >= HEIGHT || z >= SIZE || x < 0 || y < 0 || z < 0;
    }

    public static boolean isOutOfBounds(int x, int z){
        return x >= SIZE || z >= SIZE || x < 0 || z < 0;
    }

}
