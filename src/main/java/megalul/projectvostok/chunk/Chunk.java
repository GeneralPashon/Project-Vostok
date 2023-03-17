package megalul.projectvostok.chunk;

public class Chunk{

    protected final ChunkProvider providerOf;

    private final ChunkPos position;
    private final ChunkField blocks;


    public Chunk(ChunkProvider providerOf, ChunkPos position){
        this.providerOf = providerOf;

        this.position = position;
        blocks = new ChunkField(this);
    }


    public ChunkPos getPos(){
        return position;
    }

    public ChunkField getField(){
        return blocks;
    }

}
