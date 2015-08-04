package au.org.ala.phyloviz

class WizardController {
    def treeService
    def phyloService
    def userService
    def authService
    def utilsService

    static allowedMethods = [pickMethod: 'POST', save: 'POST']

    def index() {
        redirect(action: 'start')
    }

    /**
     * render different views
     * @return
     */
    def pickMethod() {
        log.debug(params)
        switch (params.options) {
            case 'addTree':
                redirect(action: 'create')
                break;
            case 'searchTree':
                redirect(action: 'search')
                break;
            case 'searchTB':
                redirect(action: 'searchTB')
                break;
            case 'expertTrees':
                redirect(action: 'expertTrees')
                break;
            case 'myTrees':
                redirect(action: 'myTrees')
                break;
            case 'myViz':
                redirect(action: 'myViz')
                break;
            case 'demo':
                redirect(action: 'demo')
                break;
        }
    }

    def start() {
        def userId = authService.getUserId()
        
        //number of trees for this user
        def numberOfTrees = userId != null ? myTrees().trees.size() : 0
        
        //number of visualisations for this user
        def numberOfVisualisations = userId != null ? myViz().viz.size() : 0
        
        render(view: '/wizard/pick', contentType: 'text/html', 
                model: [numberOfTrees: numberOfTrees, numberOfVisualisations: numberOfVisualisations, loggedIn: userId != null])
    }

    def create() {
        userService.registerCurrentUser()
        def userId = authService.getUserId()
        def user = userId != null ? Owner.findByUserId(userId) : Owner.findByDisplayName('Guest')
        log.debug('creating form for user: ' + authService.getUserId())
        if (user) {
            def tree = new Tree(params);
            tree.owner = user;
            render(view: 'create', model: [back: createLink(action: 'start'), tree: tree])
        } else {
            def msg = "Failed to detect current user details. Are you logged in?"
            flash.message = msg
            redirect(controller: 'wizard', action: 'start');
        }
    }

    def save() {
        log.debug('params are ' + params)

        if( params.tree == null || params.tree == '' ){
            params.tree = request.getFile('file').inputStream.text
            log.debug( 'tree data: ' + params.tree )
        }
        def tree

        try{
            tree = treeService.createTreeInstance(params);
            if (tree && !tree.hasErrors() && tree.validate()) {
                tree.save(flush: true)
                flash.message = message(code: 'default.created.message',
                        args: [message(code: 'tree.label', default: 'Tree'), tree.id])
                redirect(controller: 'tree', action: 'mapOtus', id: tree.id)
            } else {
                log.debug('error creating tree')
                flash.message = 'Error saving tree to database.'
                render(view: 'create', model: [back: createLink(action: 'start'), tree: tree])
            }
        } catch ( Exception e){
            log.debug('error creating tree')
            flash.message = 'Error creating/storing tree to database. Check if tree format is correct?'
            render(view: 'create', model: [back: createLink(action: 'start'), tree: tree])
        }

//        userService.registerCurrentUser()
//        def user = Owner.findByUserId(authService.getUserId()?:-1)
//        params.owner = user
//        def tree = new Tree( params )

    }

    def visualize() {
        def tree = Tree.findById(params.id)
        def nex = new Nexson(tree.getNexson())
        def viz, id, treeId = params.treeId;
        def treeIds = nex.getTreeList()
        def owner = userService.registerCurrentUser();

        if ((treeIds.size() > 1) && (params.treeId == null)) {
            redirect(action: 'pickTree', params: params)
        } else {
            treeId = treeId ?: treeIds[0].id
            viz = phyloService.createVisualization(tree.id, treeId, owner);
            id = viz.getId();
            redirect(controller: 'phylo', action: 'show', params: [id: viz.id])
        }
    }

    def pickTree() {
        def study = Tree.findById(params.id)
        def nex = new Nexson(tree.getNexson())
        def treeIds = nex.getTreeList()

        render(view: 'pickTree', model: [treeIds: treeIds])
    }

    def search() {

    }

    def treebase() {

    }

    def expertTrees() {
        def expertTrees = Tree.findAllByExpertTree(true)
        log.debug(expertTrees)
        render(view: 'listExpertTrees', model: [trees: expertTrees])
    }

    /**
     *
     */
    def myTrees() {

        def owner = userService.registerCurrentUser();
        def name;
        if (owner == null) {
            flash.message = 'Something went wrong while registering you. Are you logged in?'
            name = 'Your'
        } else {
            name = owner.getDisplayName() + "'s"
        }
        def myTrees = Tree.findAllByOwner(owner ?: -1)?:[]
        if (myTrees.size() == 0) {
            flash.message = 'You do not have any trees uploaded.'
        }
        [trees: myTrees, name: name]
    }

    /**
     *
     */
    def myViz() {

        def owner = userService.registerCurrentUser();
        def name;
        if (owner == null) {
            flash.message = 'Something went wrong while registering you. Are you logged in?'
            name = 'Your'
        } else {
            name = owner.getDisplayName() + "'s"
        }
        def myViz = Phylo.findAllByOwner(owner ?: -1)?:[]
        if (myViz.size() == 0) {
            flash.message = 'You have not created any visualisations.'
        }
        [viz: myViz, name: name, isDemonstration: false]
    }

    /**
     * displays a ui to search TreeBASE
     */
    def searchTB(){

    }

    /**
     * TreeBASE study import
     */
    def importTB(){
        def url = params.url
        params.tree = treeService.importTB(url)
        log.debug('nexml value')
        log.debug( params.tree )
        params.treeFormat = 'nexml'
        forward( action: 'save', params: params)
    }

    /**
     * demo
     */
    def demo(){
        def owner = utilsService.guestAccount();
        def name;
        if (owner == null) {
            flash.message = 'No demonstration visualisations found.'
        }
        def myViz = Phylo.findAllByOwner(owner ?: -1)?:[]
        if (myViz.size() == 0) {
            flash.message = 'You do not have any trees uploaded.'
        }
        render(view:'myViz', model: [viz: myViz, name: 'Demonstration', isDemonstration: true])
    }
}