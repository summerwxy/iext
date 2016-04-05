package iext

import grails.converters.*

class RoomController {

    // 不可随便改名字跟顺序, 会影响旧资料
    def rooms = ['大会议室', '厂长办公室']     

    def index() {
        def dateLong = 0
        if (params.date) {
            dateLong = _.dateString2Long(params.date, 'yyyy/MM/dd')
        } else {
            dateLong = Calendar.getInstance().getTimeInMillis()
        }
        return ['rooms': rooms as JSON, dateLong: dateLong] 
    }

    def test() {}

    def show() {
        /*
        def result = [:]
        def b = IwillBooking.get(params.id)
        if (b) {
            def startLong = _.dateString2Long(b.startTime)
            def endLong = _.dateString2Long(b.endTime)
            result.date = _.long2DateString(startLong, 'yyyy/MM/dd')
            result.week = _.long2DateString(startLong, 'EEE')
            result.start = _.long2DateString(startLong, 'HH:mm')
            result.end = _.long2DateString(endLong, 'HH:mm')
            result.title = b.title
            result.body = b.body
            result.room = rooms[b.userId]
        }
        ['result': result]
        */
    }

    def create_event() {
        /*
        def booking = new IwillBooking(params)
        booking.startTime = _.long2DateString(params.start.toLong())
        booking.endTime = _.long2DateString(params.end.toLong())
        booking.save()
        render (contentType: 'text/json') {['id': booking.id]}
        */
    }

    def delete_event() {
        /*
        def b = IwillBooking.get(params.id)
        def result = 'ERROR'
        if (b) {
            b.delete()
            result = 'OK'
        }
        render (contentType: 'text/json') {['result': result]}
        */
    }

    def update_event() {
        /*
        def b = IwillBooking.get(params.id)
        def result = 'ERROR'
        if (b) {
            b.properties = params
            b.startTime = _.long2DateString(params.start.toLong())
            b.endTime = _.long2DateString(params.end.toLong())
            b.save()
            result = 'OK'
        }
        render (contentType: 'text/json') {['result': result]}
        */
    }

    def get_events() {
        /*
        def result = []
        def bs = IwillBooking.findAllByStartTimeGreaterThanAndEndTimeLessThan(_.long2DateString(params.start.toLong()), _.long2DateString(params.end.toLong())) 
        bs.each {
            // TODO: 过去的会议, 更新时间超过三天 不允许修改
            def foo = [:]
            foo.id = it.id
            foo.start = _.dateString2Long(it.startTime)
            foo.end = _.dateString2Long(it.endTime)
            foo.title = it.title
            foo.body = it.body
            foo.userId = it.userId
            result << foo
        }
        render (contentType: 'text/json') {result}
        */
    }

}
