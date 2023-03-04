package megalul.projectvostok;

public class Chunk{

    private final ChunkPos position;

    public Chunk(ChunkPos position){
        this.position = position;
    }


    public ChunkPos getPos(){
        return position;
    }

}
