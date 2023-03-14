package megalul.projectvostok.chunk;

public class Chunk{

    private final ChunkPos position;
    private final ChunkField blocks;

    public Chunk(ChunkPos position){
        this.position = position;
        blocks = new ChunkField();
    }


    public ChunkPos getPos(){
        return position;
    }

    public ChunkField getBlocks(){
        return blocks;
    }

}
