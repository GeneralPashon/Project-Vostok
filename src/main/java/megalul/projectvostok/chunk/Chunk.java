package megalul.projectvostok.chunk;

public class Chunk extends ChunkUtils{

    private final ChunkPos position;
    private final ChunkField blocks;
    private ChunkState state;

    public Chunk(ChunkPos position){
        this.position = position;
        blocks = new ChunkField();
        state = ChunkState.INITIAL;
    }


    public ChunkPos getPos(){
        return position;
    }

    public ChunkField getBlocks(){
        return blocks;
    }

    public ChunkState getState(){
        return state;
    }

    public void setState(ChunkState state){
        this.state = state;
    }

}
