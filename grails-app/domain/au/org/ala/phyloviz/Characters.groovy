package au.org.ala.phyloviz

class Characters {
    Owner owner
    static belongsTo = [Owner]
    String title
    String drid
    List characterTraits = new ArrayList()
	Boolean canDelete
    static hasMany = [ 
		characterTraits: CharacterTrait 
	]
    static mapping = {
        characterTrait cascade:"all"
		canDelete defaultValue: true
    }	
    static constraints = {
        owner(nullable: false)
        title(nullable: false)
        drid(nullable: false)
		canDelete(nullable: true)
    }
}