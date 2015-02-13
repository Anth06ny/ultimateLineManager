package greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table PLAYER_BEAN.
 */
public class PlayerBean implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String name;
    private String firstname;
    private String surname;
    private int role;
    private boolean sexe;

    public PlayerBean() {
    }

    public PlayerBean(Long id) {
        this.id = id;
    }

    public PlayerBean(Long id, String name, String firstname, String surname, int role, boolean sexe) {
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.surname = surname;
        this.role = role;
        this.sexe = sexe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public boolean getSexe() {
        return sexe;
    }

    public void setSexe(boolean sexe) {
        this.sexe = sexe;
    }

}
