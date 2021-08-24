package Holders;

public class User {
    private String name,email,pass,cpass;

   public User()
   {

   }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getCpass() { return cpass; }

    public void setCpass(String cpass) {
        this.cpass = cpass;
    }
}
