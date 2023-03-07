package megalul.projectvostok;

public class World{

    private final ChunkProvider chunkProvider;

    public World(Main session){
        chunkProvider = new ChunkProvider(session);
    }

    public ChunkProvider getChunks(){
        return chunkProvider;
    }



}
