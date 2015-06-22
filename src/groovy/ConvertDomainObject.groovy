package au.org.ala.phyloviz

/**
 * Created by Temi Varghese on 19/06/15.
 */
class ConvertDomainObject {


    protected Map<String, String> prop;

    public convert(source, target) {
        prop.each { key, value ->
            try {
                target[value] = source[key];
            } catch (Exception e) {
                target[value] = null;
            }
        }

        return target;
    }

    public convert( source) {
        return convert(source, [:]);
    }

    public clone(Object source) {
        Map<String, String> target = [:]
        source.each { key, value ->
            try {
                target[value] = source[key];
            } catch (Exception e) {
                target[value] = null;
            }
        }
    }
}
