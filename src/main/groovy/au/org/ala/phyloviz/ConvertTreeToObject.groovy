package au.org.ala.phyloviz
/**
 * Created by Temi Varghese on 2/04/15.
 */
class ConvertTreeToObject {
    private static final Map<String, String> prop = [
            'id':'studyId',
            'expertTreeTaxonomy':'group',
            'tree':'tree',
            'treeFormat':'treeFormat',
            'reference':'studyName',
            'expertTreeTaxonomy':'focalClade',
            'year':'year',
            'title':'authors',
            'doi':'doi',
            'numberOfLeaves':'numberOfLeaves',
            'numberOfInternalNodes':'numberOfInternalNodes',
            'treeId':'treeId',
            'notes':'notes'
    ]
    public convert(Tree source, target){
        prop.each { key, value ->
            try{
                target[value] = source[key];
            } catch (Exception e){
                target[value] = null;
            }
        }

        return target;
    }

    public convert(Tree source){
        return convert(source,[:]);
    }

    public clone(Object source){
        Map<String,String> target = [:]
        source.each { key, value ->
            try{
                target[value] = source[key];
            } catch (Exception e){
                target[value] = null;
            }
        }
    }
}
