package megalul.projectvostok;

public class ChunkBuilder{

    public static float[] build(Chunk chunk){
        return new float[]{
            0 ,0,0 , 1,1,1,1, 0,0,
            16,0,0 , 1,1,1,1, 1,0,
            16,0,16, 1,1,1,1, 1,1,
            16,0,16, 1,1,1,1, 1,1,
            0 ,0,16, 1,1,1,1, 0,1,
            0 ,0,0 , 1,1,1,1, 0,0,
        };
    }

}
