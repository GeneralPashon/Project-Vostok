package megalul.projectvostok;

public class Chunk{

    public static final int CHUNK_SIZE = 16;


    private final ChunkPos position;

    public Chunk(ChunkPos position){
        this.position = position;
    }


    public ChunkPos getPos(){
        return position;
    }

}
