package megalul.projectvostok.block;

public class Air extends BlockProperties{

    protected Air(int id){
        super(id);
    }

    @Override
    public boolean isSolid(){
        return false;
    }

}