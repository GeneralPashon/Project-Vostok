package megalul.projectvostok.chunk.data;

import megalul.projectvostok.GameCamera;

import java.util.Objects;

import static megalul.projectvostok.chunk.ChunkUtils.HEIGHT;
import static megalul.projectvostok.chunk.ChunkUtils.SIZE;

public class ChunkPos{

    public final int x, z;

    public ChunkPos(int x, int z){
        this.x = x;
        this.z = z;
    }


    public boolean isInFrustum(GameCamera camera){
        return camera.getFrustum().isBoxInFrustum(
            x * SIZE, 0, z * SIZE,
            x * SIZE + SIZE, HEIGHT, z * SIZE + SIZE
        );
    }

    public ChunkPos getNeighbor(int x, int z){
        return new ChunkPos(this.x + x, this.z + z);
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
