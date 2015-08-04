/**
 * Created by Temi Varghese on 30/07/15.
 */
describe("Habitat tests", function () {
    var hb;

    // make sure the order of object injection is the same as the argument list in the function
    beforeAll(inject( 'habitat', function ($hb) {
        hb = $hb;
        console.log("****** Habitat Tests ******")
    }));

    afterAll(function () {
        console.log("----------------------------");
    });


    beforeEach(function(){

    });
});