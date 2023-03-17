package megalul.projectvostok.chunk;

import java.util.Objects;

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


    @Override
    public boolean equals(Object object){
        if(object == this)
            return true;
        if(object == null || object.getClass() != getClass())
            return false;
        Chunk chunk = (Chunk) object;
        return position.equals(chunk.position);
    }

    @Override
    public int hashCode(){
        return position.hashCode();
    }

}
