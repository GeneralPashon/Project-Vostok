package megalul.projectvostok.block;

public abstract class BlockProperties{

    private final int id;

    protected BlockProperties(int id){
        this.id = id;
    }

    public int getID(){
        return id;
    }

    public abstract boolean isSolid();

}
