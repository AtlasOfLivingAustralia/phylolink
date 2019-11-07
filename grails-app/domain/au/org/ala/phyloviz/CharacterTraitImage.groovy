package au.org.ala.phyloviz

class CharacterTraitImage {

    Owner owner
	String name
	byte[] data
    static belongsTo = [Owner]

    static mapping = {
        description type:'text'
		columns { 
			data type:'blob'
		}
    }
    static constraints = {
        owner(nullable: false)
        name(nullable: false)
        data(nullable: false, maxSize: 1024 * 1024 * 2) // Limit upload file size to 2MB
    }
}