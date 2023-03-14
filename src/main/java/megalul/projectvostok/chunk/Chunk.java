package megalul.projectvostok.chunk;

import java.util.BitSet;

public class Chunk{

    protected final ChunkProvider providerOf;

    private final ChunkPos position;
    private final ChunkField blocks;
    protected volatile BitSet neighbors;


    public Chunk(ChunkProvider providerOf, ChunkPos position){
        this.providerOf = providerOf;

        this.position = position;
        blocks = new ChunkField(this);

        neighbors = new BitSet(4); // +z, -z, +x, -x
    }


    public ChunkPos getPos(){
        return position;
    }

    public ChunkField getBlocks(){
        return blocks;
    }

}
