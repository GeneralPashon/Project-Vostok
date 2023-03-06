package megalul.projectvostok;

public class ChunkSection{

    public static final int SIZE = 16;

    private final ChunkBlocks blocks;

    public ChunkSection(){
        blocks = new ChunkBlocks();
    }


    public ChunkBlocks getBlocks(){
        return blocks;
    }

}
