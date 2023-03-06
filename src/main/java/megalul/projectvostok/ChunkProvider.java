package megalul.projectvostok;

import glit.Glit;
import glit.graphics.texture.Texture;
import glit.graphics.util.batch.Batch;
import glit.math.Maths;
import glit.math.vecmath.vector.Vec2f;
import glit.math.vecmath.vector.Vec3f;
import glit.util.Utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkProvider{

    public static final Chunk EMPTY_CHUNK = null;

    private final Main session;

    private final ConcurrentMap<ChunkPos, Chunk> chunkList;
    private final ConcurrentMap<ChunkPos, ChunkMesh> meshList;
    private final CopyOnWriteArrayList<ChunkPos> chunkAddingQueue;

    private volatile boolean queueIsSorted;


    public ChunkProvider(Main session){
        this.session = session;

        chunkList = new ConcurrentHashMap<>();
        meshList = new ConcurrentHashMap<>();

        chunkAddingQueue = new CopyOnWriteArrayList<>();

        Thread thread1 = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                updateChunks();
            }
        }, "Update Chunks Thread");
        thread1.setDaemon(true);
        thread1.start();

        Thread thread2 = new Thread(()->{
            while(!queueIsSorted);
            while(!Thread.currentThread().isInterrupted()){
                loadChunks();
            }
        }, "Load Chunks Thread");
        thread2.setDaemon(true);
        thread2.start();

        Thread thread3 = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                unloadChunks();
            }
        }, "Unload Chunks Thread");
        thread3.setDaemon(true);
        thread3.start();
    }


    private void updateChunks(){
        Vec3f camPos = getCamPos();
        int renderDist = session.getOptions().getRenderDistance();

        int beginX = Maths.floor(camPos.x) - renderDist;
        int beginZ = Maths.floor(camPos.z) - renderDist;

        boolean needToSort = false;

        for(int x = beginX; x < beginX + 1 + renderDist * 2; x++){
            for(int z = beginZ; z < beginZ + 1 + renderDist * 2; z++){
                if(isOffTheGrid(x, z))
                    continue;

                Chunk chunk = getChunk(x, z);
                if(chunk != EMPTY_CHUNK)
                    continue;

                ChunkPos chunkPos = new ChunkPos(x, z);
                if(!chunkPos.isInFrustum(session.getCamera()))
                    continue;

                if(!chunkAddingQueue.contains(chunkPos)){
                    chunkAddingQueue.add(chunkPos);
                    needToSort = true;
                }
            }
        }

        if(needToSort){
            chunkAddingQueue.sort((pos1, pos2)->Maths.round(distToChunk(pos1.x, pos1.z, camPos) - Maths.round(distToChunk(pos2.x, pos2.z, camPos))));
            queueIsSorted = true;
        }
    }

    private void loadChunks(){
        for(ChunkPos chunkPos: chunkAddingQueue){
            chunkAddingQueue.remove(chunkPos);
            if(chunkPos == null || isOffTheGrid(chunkPos))
                continue;

            loadChunk(chunkPos);

            if(queueIsSorted){
                queueIsSorted = false;
                break;
            }
        }
    }

    public void unloadChunks(){
        chunkAddingQueue.removeIf(this::isOffTheGrid);

        for(ChunkPos chunkPos: chunkList.keySet())
            if(isOffTheGrid(chunkPos))
                unloadChunk(chunkPos);
    }


    public void loadChunk(ChunkPos chunkPos){
        Utils.delayMillis(2);
        chunkList.put(chunkPos, new Chunk(chunkPos));
    }

    public void unloadChunk(ChunkPos chunkPos){
        chunkList.remove(chunkPos);
    }


    public void draw(Batch batch, Texture texture){
        int chunkSize = 10;
        int offsetX = Glit.getWidth() / 2 - chunkSize / 2;
        int offsetY = Glit.getHeight() / 2 - chunkSize / 2;

        for(Chunk chunk: chunkList.values()){
            if(chunk.getPos().isInFrustum(session.getCamera()))
                batch.setColor(0.5F, 0.5F, 1, 0.5F);
            else
                batch.setColor(1, 1, 1, 0.5F);

            batch.draw(
                texture,
                chunk.getPos().x * chunkSize + offsetX,
                chunk.getPos().z * chunkSize + offsetY,
                chunkSize, chunkSize
            );
        }

        Vec3f camPos = getCamPos();
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


    private boolean isOffTheGrid(int x, int z){
        return distToChunk(x, z, getCamPos()) > session.getOptions().getRenderDistance();
    }

    private boolean isOffTheGrid(ChunkPos chunkPos){
        return isOffTheGrid(chunkPos.x, chunkPos.z);
    }


    private float distToChunk(int x, int z, Vec3f camPos){
        return Vec2f.len(x - camPos.x + 0.5F, z - camPos.z + 0.5F);
    }

    private Vec3f getCamPos(){
        return session.getCamera().getPos().clone().div(Chunk.CHUNK_SIZE);
    }

}
