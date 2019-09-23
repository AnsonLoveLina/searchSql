import java.util.List;

/**
 * Created by zy-xx on 2019/9/23.
 */
public class MyIndexDynamic {
    private String fieldA;
    private List parent;
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFieldA() {
        return fieldA;
    }

    public void setFieldA(String fieldA) {
        this.fieldA = fieldA;
    }

    public List getParent() {
        return parent;
    }

    public void setParent(List parent) {
        this.parent = parent;
    }
}
