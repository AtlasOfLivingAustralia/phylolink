package au.org.ala.phyloviz

import org.apache.commons.lang3.StringUtils

class CharacterTrait {

	public enum TraitType {
		Continuous( 'Continuous' ),
		Integer( 'Integer' ),
		Discrete( 'Discrete' ),
		Categorical( 'Categorical' )
		final String value;
		TraitType(String value){
			this.value = value
		}
		String toString(){
			value;
		}
		String getKey(){
			name()
		}
		public static TraitType forValue(String value) {
			if (StringUtils.isBlank(value)) {
				return null;
			}
			for (TraitType traitType : values()) {
				if (traitType.toString().equalsIgnoreCase(value)) {
					return traitType;
				}
			}
			throw new RuntimeException("Invalid TraitType value=" + value);
		}
		public static TraitType forKey(String key) {
			if (StringUtils.isNotBlank(key)) {
				for (TraitType traitType : values()) {
					if (traitType.getKey().equalsIgnoreCase(key)) {
						return traitType;
					}
				}
			}
			throw new RuntimeException("Invalid TraitType key=" + key);
		}
	}

    Characters characters
    static belongsTo = [Characters]
    String title
    String description
    String type
    CharacterTraitImage image

    static mapping = {
        description type:'text'
	}
    static constraints = {
        characters(nullable: false)
        title(nullable: false)
        description(nullable: true)
        type(nullable: true)
        image(nullable: true)
    }

	TraitType getTraitType() { 
		type ? TraitType.forKey(type) : null 
	}
	void setTraitType(TraitType traitType) { 
		type = traitType ? traitType.getKey() : null
	}

	static transients = ['traitType']	
}