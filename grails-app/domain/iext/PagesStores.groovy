package iext

class PagesStores {

    String sid
    String businessName
    String branchName
	String address
    String telephone
    String categories
	String city
    String province
    long offsetType
    double longitude
    double latitude
    String photo1
    String photo2
    String photo3
    String photo4
    String photo5
    String photo6
    String introduction
    String recommend
    String special
    String openTime
    double avgPrice
    long poiId
    long availableState
    String district
    long updateStatus

    // system data
    Date dateCreated
    Date lastUpdated

    static constraints = {
        introduction(maxSize:1500)
    }
}
