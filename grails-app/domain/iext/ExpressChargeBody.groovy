package iext

class ExpressChargeBody {

    
    static belongsTo = [ h: ExpressChargeHead ]

    String vid

    // system data
    Date dateCreated
    Date lastUpdated


    static constraints = {

    }

    static mapping = {
        // table "mooncake2expressd"
    }

}
