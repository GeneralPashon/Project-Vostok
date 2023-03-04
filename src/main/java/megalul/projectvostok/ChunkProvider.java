package megalul.projectvostok;

import glit.Glit;
import glit.graphics.texture.Texture;
import glit.graphics.util.Batch;
import glit.math.Mathc;
import glit.math.Maths;
import glit.math.vecmath.vector.Vec2f;
import glit.math.vecmath.vector.Vec3f;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChunkProvider{

    public static final Chunk EMPTY_CHUNK = null;

    private final Main session;

    private final ConcurrentMap<ChunkPos, Chunk> chunkList;
    private final ConcurrentMap<ChunkPos, ChunkMesh> meshList;

    public ChunkProvider(Main session){
        this.session = session;

        chunkList = new ConcurrentHashMap<>();
        meshList = new ConcurrentHashMap<>();

        Thread thread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                update();
                Thread.yield();
            }
        }, "Update Chunks Thread");
        thread.setDaemon(true);
        thread.start();
    }


    private void update(){
        Vec3f camPos = session.getCamera().getPos();
        int renderDist = session.getOptions().getRenderDistance();

        int beginX = Maths.floor(camPos.x) - renderDist;
        int beginZ = Maths.floor(camPos.z) - renderDist;

        for(ChunkPos chunkPos: chunkList.keySet())
            if(!isAccess(chunkPos))
                chunkList.remove(chunkPos);

        for(int x = beginX; x < beginX + 1 + renderDist * 2; x++){
            for(int z = beginZ; z < beginZ + 1 + renderDist * 2; z++){
                if(!isAccess(x, z))
                    continue;

                Chunk chunk = getChunk(x, z);
                if(chunk != EMPTY_CHUNK)
                    continue;

                ChunkPos chunkPos = new ChunkPos(x, z);
                chunkList.put(
                    chunkPos,
                    new Chunk(chunkPos)
                );
            }
        }
    }

    public void draw(Batch batch, Texture texture){
        int chunkSize = 10;
        int offsetX = Glit.getWidth() / 2 - chunkSize / 2;
        int offsetY = Glit.getHeight() / 2 - chunkSize / 2;

        batch.setAlpha(0.5F);
        for(Chunk chunk: chunkList.values())
            batch.draw(
                texture,
                chunk.getPos().x * chunkSize + offsetX,
                chunk.getPos().z * chunkSize + offsetY,
                chunkSize, chunkSize
            );

        Vec3f camPos = session.getCamera().getPos();
        batch.setColor(1, 1, 1, 1);
        int camSize = chunkSize / 4;
        batch.draw(
            texture,
            camPos.x * chunkSize - camSize / 2F + offsetX,
            camPos.z * chunkSize - camSize / 2F + offsetY,
            camSize, camSize
        );
        batch.resetColor();
    }


    public Chunk getChunk(ChunkPos chunkPos){
        return chunkList.getOrDefault(chunkPos, EMPTY_CHUNK);
    }

    public Chunk getChunk(int x, int z){
        ChunkPos chunkPos = chunkList.keySet().stream().filter(pos->pos.x == x && pos.z == z).findAny().orElse(null);
        if(chunkPos == null)
            return EMPTY_CHUNK;

        return chunkList.getOrDefault(chunkPos, EMPTY_CHUNK);
    }


    public boolean isAccess(int x, int z){
        Vec3f camPos = session.getCamera().getPos();
        float toCamDist = (float) Vec2f.len(x - camPos.x + 0.5F, z - camPos.z + 0.5F);
        return toCamDist <= session.getOptions().getRenderDistance();
    }

    public boolean isAccess(ChunkPos chunkPos){
        return isAccess(chunkPos.x, chunkPos.z);
    }

}
