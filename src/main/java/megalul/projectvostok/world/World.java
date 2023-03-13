package megalul.projectvostok.world;

import megalul.projectvostok.Main;
import megalul.projectvostok.chunk.ChunkProvider;

public class World{

    private final ChunkProvider chunkProvider;

    public World(Main session){
        chunkProvider = new ChunkProvider(session);
    }

    public ChunkProvider getChunks(){
        return chunkProvider;
    }



}
