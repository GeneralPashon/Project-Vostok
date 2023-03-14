package megalul.projectvostok.chunk;

import glit.math.Maths;
import glit.math.vecmath.vector.Vec2f;
import glit.math.vecmath.vector.Vec3f;
import glit.util.time.FpsCounter;
import glit.util.time.Sync;
import megalul.projectvostok.Main;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChunkProvider{

    public static final Chunk EMPTY_CHUNK = null;
    private static final int MAX_LOADS_PER_TICK = 64;

    private final Main session;

    private final ConcurrentMap<ChunkPos, Chunk> loadedChunkList;
    private final CopyOnWriteArrayList<ChunkPos> chunkLoadQueue;
    private final Set<ChunkPos> chunksToLoadQueue;

    private final CopyOnWriteArrayList<Chunk> chunksToBuildQueue;
    private final ConcurrentMap<ChunkPos, float[]> builtChunksList;
    private final ConcurrentMap<ChunkPos, ChunkMesh> meshList;

    public final FpsCounter updateTps, loadTps, unloadTps, buildTps;
    private final Sync updateSync, loadSync, unloadSync, buildSync;


    public ChunkProvider(Main session){
        this.session = session;

        loadedChunkList = new ConcurrentHashMap<>();

        chunksToBuildQueue = new CopyOnWriteArrayList<>();
        builtChunksList = new ConcurrentHashMap<>();
        meshList = new ConcurrentHashMap<>();

        chunksToLoadQueue = new HashSet<>();
        chunkLoadQueue = new CopyOnWriteArrayList<>();

        updateTps = new FpsCounter();
        loadTps = new FpsCounter();
        unloadTps = new FpsCounter();
        buildTps = new FpsCounter();

        updateSync = new Sync(40);
        loadSync = new Sync(40);
        unloadSync = new Sync(40);
        buildSync = new Sync(40);

        Thread updateThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                updateTps.update();
                updateSync.sync();
                updateChunks();
            }
        }, "Update Chunks Thread");
        updateThread.setDaemon(true);
        updateThread.start();

        Thread loadThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                loadTps.update();
                loadSync.sync();
                loadChunks();
            }
        }, "Load Chunks Thread");
        loadThread.setDaemon(true);
        loadThread.start();

        Thread unloadThread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                unloadTps.update();
                unloadSync.sync();
                unloadChunks();
            }
        }, "Unload Chunks Thread");
        unloadThread.setDaemon(true);
        unloadThread.start();

        Thread updateThread2 = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                buildTps.update();
                buildSync.sync();
                buildChunks();
            }
        }, "Build Chunks Thread");
        updateThread2.setDaemon(true);
        updateThread2.start();
    }


    private void updateChunks(){
        Vec3f camPos = getCamPos();
        int renderDist = session.getOptions().getRenderDistance();

        int beginX = Maths.floor(camPos.x) - renderDist;
        int beginZ = Maths.floor(camPos.z) - renderDist;
        int endX = beginX + 1 + renderDist * 2;
        int endZ = beginZ + 1 + renderDist * 2;

        for(int x = beginX; x < endX; x++){
            for(int z = beginZ; z < endZ; z++){
                if(isOffTheGrid(x, z))
                    continue;

                Chunk chunk = getChunk(x, z);
                if(chunk != null)
                    continue;

                ChunkPos chunkPos = new ChunkPos(x, z);
                if(!chunkPos.isInFrustum(session.getCamera()) || chunkLoadQueue.contains(chunkPos) || loadedChunkList.containsKey(chunkPos))
                    continue;

                chunksToLoadQueue.add(chunkPos);
            }
        }

        if(chunksToLoadQueue.size() != 0){
            chunkLoadQueue.addAll(chunksToLoadQueue);
            chunkLoadQueue.sort((pos1, pos2)->Maths.round(distToChunk(pos1.x, pos1.z, camPos) - Maths.round(distToChunk(pos2.x, pos2.z, camPos))));
            chunksToLoadQueue.clear();
        }
    }

    private void loadChunks(){
        int chunkLoadedNum = 0;
        for(ChunkPos chunkPos: chunkLoadQueue){
            if(chunkLoadedNum >= MAX_LOADS_PER_TICK)
                break;

            chunkLoadQueue.remove(chunkPos);
            if(chunkPos == null || isOffTheGrid(chunkPos))
                continue;
            loadChunk(chunkPos);
            chunkLoadedNum++;
        }
    }

    public void unloadChunks(){
        chunkLoadQueue.removeIf(this::isOffTheGrid);

        for(ChunkPos chunkPos: loadedChunkList.keySet())
            if(isOffTheGrid(chunkPos))
                unloadChunk(chunkPos);
    }

    private void buildChunks(){
        for(Chunk chunk: chunksToBuildQueue){
            float[] vertices = ChunkBuilder.build(chunk);
            builtChunksList.put(chunk.getPos(), vertices);

            chunksToBuildQueue.remove(chunk);
        }
    }


    public void loadChunk(ChunkPos chunkPos){
        Chunk chunk = new Chunk(chunkPos);
        ChunkGenerator.generate(chunk);

        chunksToBuildQueue.add(chunk);
        loadedChunkList.put(chunkPos, chunk);
    }

    public void unloadChunk(ChunkPos chunkPos){
        loadedChunkList.remove(chunkPos);
    }


    public void updateMeshes(){
        for(Map.Entry<ChunkPos, float[]> entry: builtChunksList.entrySet()){
            loadChunkMesh(entry.getKey(), entry.getValue());
            builtChunksList.remove(entry.getKey());
        }

        for(ChunkPos chunkPos: meshList.keySet())
            if(loadedChunkList.get(chunkPos) == null){
                meshList.get(chunkPos).dispose();
                meshList.remove(chunkPos);
            }
    }

    public void loadChunkMesh(ChunkPos chunkPos, float[] vertices){
        ChunkMesh mesh = meshList.get(chunkPos);
        if(mesh == null)
            mesh = new ChunkMesh();

        mesh.setVertices(vertices);
        meshList.put(chunkPos, mesh);
    }


    public Chunk getChunk(ChunkPos chunkPos){
        return loadedChunkList.getOrDefault(chunkPos, EMPTY_CHUNK);
    }

    public Chunk getChunk(int x, int z){
        ChunkPos chunkPos = loadedChunkList.keySet().stream().filter(pos->pos.x == x && pos.z == z).findAny().orElse(null);
        if(chunkPos == null)
            return EMPTY_CHUNK;

        return loadedChunkList.getOrDefault(chunkPos, EMPTY_CHUNK);
    }

    public Collection<Chunk> getChunks(){
        return loadedChunkList.values();
    }

    public Collection<Map.Entry<ChunkPos, ChunkMesh>> getMeshes(){
        return meshList.entrySet();
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
        return session.getCamera().getPos().clone().div(ChunkUtils.SIZE);
    }

}
