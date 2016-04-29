package ismartdev.mn.darkhanwindow.model;

/**
 * Created by Ulzii on 4/14/2016.
 */
public class Category {
    private int term_id;
    private String name;
    private int term_taxonomy_id;

    public int getTerm_taxonomy_id() {
        return term_taxonomy_id;
    }

    public void setTerm_taxonomy_id(int term_taxonomy_id) {
        this.term_taxonomy_id = term_taxonomy_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTerm_id() {
        return term_id;
    }

    public void setTerm_id(int term_id) {
        this.term_id = term_id;
    }



}
