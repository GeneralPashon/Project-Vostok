package megalul.projectvostok;

import java.util.Objects;

public class ChunkPos{

    public final int x, z;

    public ChunkPos(int x, int z){
        this.x = x;
        this.z = z;
    }


    public boolean isInFrustum(GameCamera camera){
        return camera.getFrustum().isBoxInFrustum(
            x * Chunk.CHUNK_SIZE, 0, z * Chunk.CHUNK_SIZE,
            x * Chunk.CHUNK_SIZE + Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, z * Chunk.CHUNK_SIZE + Chunk.CHUNK_SIZE
        );
    }


    @Override
    public boolean equals(Object object){
        if(object == this)
            return true;
        if(object == null || object.getClass() != getClass())
            return false;
        ChunkPos chunkPos = (ChunkPos) object;
        return x == chunkPos.x && z == chunkPos.z;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, z);
    }

}
