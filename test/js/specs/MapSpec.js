/**
 * Created by Temi Varghese on 30/07/15.
 */
describe("Map tests", function () {
    var map;

    // make sure the order of object injection is the same as the argument list in the function
    beforeAll(inject( 'map', function ($map) {
        map = $map;
        console.log("****** Map Tests ******")
    }));

    afterAll(function () {
        console.log("----------------------------");
    });


    beforeEach(function(){

    });

    it("example test", function () {
        var charName = {name:'test'};
        expect(map.convertCharacterToColorby(charName)).toEqual({
            'displayName':'test',
            'name': 'test',
            'type': 'character'
        })
    });
});