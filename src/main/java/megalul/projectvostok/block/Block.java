package megalul.projectvostok.block;

public enum Block{

    AIR(new Air(0)),
    DIRT(new Dirt(1)),
    GRASS_BLOCK(new GrassBlock(2));


    public final int id;
    public final BlockProperties properties;

    Block(BlockProperties properties){
        this.id = properties.getID();
        this.properties = properties;
    }

    public static Block fromID(int id){
        return Block.values()[id];
    }

}
