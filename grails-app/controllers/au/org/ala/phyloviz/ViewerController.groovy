package au.org.ala.phyloviz

class ViewerController {

    def index() {

    }

    def show(){
        def viewer = params.viewer?:'phylojive'
        switch ( viewer ){
            case 'phylojive':
                render( view: 'phylojive', model: [ studyId: params.studyId, treeId: params.treeId , tree: params.tree ])
                break;
        }
    }
}
