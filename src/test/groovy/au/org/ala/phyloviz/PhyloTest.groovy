package au.org.ala.phyloviz

import java.util.List

class PhyloTest extends GroovyTestCase {

    void testParseGenusFromSpecies() {
		assertToString(Phylo.parseGenusFromSpecies("A B"), "A")
		assertToString(Phylo.parseGenusFromSpecies("AB"), "AB")
		assertToString(Phylo.parseGenusFromSpecies("A B_C"), "A")
		assertToString(Phylo.parseGenusFromSpecies("A_B C"), "A")
		assertToString(Phylo.parseGenusFromSpecies("A B C"), "A")		
		assertToString(Phylo.parseGenusFromSpecies("Gehyra_xenopus"), "Gehyra")
		assertToString(Phylo.parseGenusFromSpecies("Gehyra xenopus"), "Gehyra")
		assertToString(Phylo.parseGenusFromSpecies("Gehyra-xenopus"), "Gehyra-xenopus")
	}

	void testListToStringWithNewLineDelimiter() {
		List<String> list = new ArrayList<>()
		assertToString(Phylo.listToStringWithNewLineDelimiter(list), "")
		list.add("1")
		assertToString(Phylo.listToStringWithNewLineDelimiter(list), "1")
		list.add("2")
		assertToString(Phylo.listToStringWithNewLineDelimiter(list), "1\r\n2")
		assertEquals(4, Phylo.listToStringWithNewLineDelimiter(list).length())
	}
}
