/**
 * Created by Temi Varghese on 29/07/15.
 */
describe("PJ tests", function () {
    var pj;

    // make sure the order of object injection is the same as the argument list in the function
    beforeAll(inject( 'pj', function ($pj) {
        pj = $pj;
        console.log("****** PJ Tests ******")
    }));

    afterAll(function () {
        console.log("----------------------------");
    });


    beforeEach(function(){

    });

    it("query id should be undefined", function () {
        expect(pj.getQid()).toBeUndefined();
    });

    it("newick parsing working correctly",function(){
        pj.setTree("(a,(c,d)b);",'newick');
        var root = pj.getRoot(),
            node = pj.getNodeById(root),
            children = pj.getChildrenName(node);
        expect(children).toEqual(['a','c','d']);
    });
});