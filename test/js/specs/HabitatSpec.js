/**
 * Created by Temi Varghese on 30/07/15.
 */
describe("Habitat tests", function () {
    var hb;

    // make sure the order of object injection is the same as the argument list in the function
    beforeAll(inject('habitat', function ($hb) {
        hb = $hb;
        console.log("****** Habitat Tests ******")
    }));

    afterAll(function () {
        console.log("----------------------------");
    });


    beforeEach(function () {

    });

    it("should initialise only the given nubmer of habitats", function () {
        var list = {
            habitats: [{
                displayName: "a",
                id: "b"
            }],
            count:0
        };

        hb.setHabitats(list);
        var state = hb.currentState(),
            hab = JSON.parse(state.json);

        expect(hab.habitats.length).toBe(1);
        expect(hab.habitats[0].id).toEqual("b");
    });
});