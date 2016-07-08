package iext

class ExpressChargeHead {


    static hasMany = [ d: ExpressChargeBody ]

    String name
    String phone
    String address
    String lv1
    String lv2
    String lv3
    float kg
    float fee
    
    String expressNo
    String status

    float lat
    float lng

    // system data
    Date dateCreated
    Date lastUpdated
    
    static constraints = {
        lat(nullable: true)        
        lng(nullable: true)        
    }

    static mapping = {
        // table "mooncake2expressh"
    }

}
