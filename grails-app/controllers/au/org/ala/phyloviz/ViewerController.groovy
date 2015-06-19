package au.org.ala.phyloviz

import grails.converters.JSON

class ViewerController {

    def index() {

    }

    def show(){
        def viewer = params.viewer?:'phylojive';
        Integer id;
        if(params.studyId){
            id = Integer.parseInt(params.studyId);
        }

        if(id){
            Tree tree = Tree.findById(id)
            switch ( viewer ){
                case 'phylojive':
                    render( view: 'phylojive', model: [ studyId: params.studyId, treeId: params.treeId , tree: tree ])
                    break;
            }
        } else {
            render( text: [ error: 'studyId not found'] as JSON);
        }
    }

    def recordsOccurrenceForm(){

    }
}
