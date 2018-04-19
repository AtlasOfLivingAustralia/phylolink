package au.org.ala.phyloviz

import au.org.ala.web.AlaSecured

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
            case 'treeAdmin':
                redirect(controller: "tree", action: 'treeAdmin')
                break;
            case 'rematchAll':
                redirect(controller: 'admin', action: 'rematchAll')
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
        log.debug('params are ' + params)

        def tree
        if (!params.tree && params.file) {
            params.tree = getTreeFromMultipartFile()
        }

        try {
            if (params.tree) {
                tree = treeService.createTreeInstance(params);
                if (tree && !tree.hasErrors() && tree.validate()) {
                    tree.save(flush: true)
                    flash.message = message(code: 'default.created.message',
                            args: [message(code: 'tree.label', default: 'Tree'), tree.id])
                    redirect(controller: 'tree', action: 'mapOtus', id: tree.id)
                } else {
                    log.debug('error creating tree')
                    flash.message = 'Error saving tree to database.'
                    render(view: 'create', model: [back: createLink(action: 'start'), tree: params])
                }
            } else {
                if (params.formSubmitted && !params.tree) {
                    flash.error = true;
                    flash.message = 'Tree not provided';
                }
                render(view: 'create', model: [back: createLink(action: 'start'), tree: params])
            }
        } catch (Exception e) {
            log.debug('error creating tree')
            flash.message = 'Error creating/storing tree to database. Check if tree format is correct?'
            render(view: 'create', model: [back: createLink(action: 'start'), tree: params])
        }
    }

    private String getTreeFromMultipartFile() {
        if (params.file) {
            return request.getFile('file')?.inputStream.text
        }
    }

    @AlaSecured(redirectController = "ala", redirectAction = "login")
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
        render(view: 'listExpertTrees', model: [trees: expertTrees, isAdmin: params.isAdmin])
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
        def myTrees = Tree.findAllByOwner(owner ?: -1) ?: []
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
        def myViz = Phylo.findAllByOwner(owner ?: -1) ?: []
        if (myViz.size() == 0) {
            flash.message = 'You have not created any visualisations.'
        }
        [viz: myViz, name: name, isDemonstration: false]
    }

    /**
     * displays a ui to search TreeBASE
     */
    def searchTB() {

    }

    /**
     * TreeBASE study import
     */
    def importTB() {
        def url = params.url
        params.tree = treeService.importTB(url)
        log.debug('nexml value')
        log.debug(params.tree)
        params.treeFormat = 'nexml'
        forward(action: 'save', params: params)
    }

    /**
     * demo
     */
    def demo() {
        def owner = utilsService.guestAccount();
        def name;
        if (owner == null) {
            flash.message = 'No demonstration visualisations found.'
        }
        def myViz = Phylo.findAllByOwner(owner ?: -1) ?: []
        if (myViz.size() == 0) {
            flash.message = 'You do not have any trees uploaded.'
        }
        render(view: 'myViz', model: [viz: myViz, name: 'Demonstration', isDemonstration: true])
    }
}