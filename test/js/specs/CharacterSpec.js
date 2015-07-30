/**
 * Created by Temi Varghese on 30/07/15.
 */
describe("Character tests", function () {
    var ch;

    // make sure the order of object injection is the same as the argument list in the function
    beforeAll(inject('character', function ($character) {
        ch = $character;
        console.log("****** Character Tests ******")
    }));

    afterAll(function () {
        console.log("----------------------------");
    });


    beforeEach(function () {

    });

    it("should provide a subset of the provided char json", function () {
        var charJson = {'acacia prainii': {'a': [1]}, 'acacia dealbata': {'a': [2]}};
        expect(ch.charJsonSubset(['acacia dealbata'], charJson)).toEqual({'acacia dealbata': {'a': [2]}})
    });
});