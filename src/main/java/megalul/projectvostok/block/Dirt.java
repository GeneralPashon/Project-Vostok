package megalul.projectvostok.block;

public class Dirt extends BlockProperties{

    protected Dirt(int id){
        super(id);
    }

    @Override
    public boolean isSolid(){
        return true;
    }

}
