package au.org.ala.phyloviz

class Owner {
    Long userId
    String email
    String displayName
    Date created               //set to the date when the user first contributed
    String role

    static constraints = {
        created maxSize: 19
        displayName maxSize: 200
        userId unique: true
    }

    String toString() {
        return this.displayName;
    }
}