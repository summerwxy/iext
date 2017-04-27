package iext

import groovy.sql.Sql
import grails.converters.*
import groovy.json.*

class ApiController {
    def dataSource

    def index() { }

    // use client ip address to get store no
    def getStoreInfo() {
        header( "Access-Control-Allow-Origin", "*" )
        
        def sql = new Sql(dataSource)
        def re = /^(172\.16\.\d+)\.\d+$/
        def finder = request.remoteAddr =~ re
        def result = [:]
        if (finder.count == 1) {
            def s = "select S_NO, S_NAME from STORE where S_IP like '" + finder[0][1] + ".%'"
            def row = sql.firstRow(s)
            result.s_no = row.S_NO
            result.s_name = row.S_NAME
        }    
        if (request.remoteAddr == '0:0:0:0:0:0:0:1' || request.remoteAddr.startsWith('192.168.')) {
            result.s_no = '8027010' // 8027010 平江店 8021001 中山店 
            result.s_name = '平江店'
        }
        render result as JSON
    }


    def pos1() {
        header( "Access-Control-Allow-Origin", "*" )

        def sql = new Sql(dataSource)
        def s = """
            select top ${params.limit} * from (
            	select a.*, b.date_created, b.op_name, b.op_no, b.s_no, ROW_NUMBER() over (order by a.id desc) as rown
            	from iwill_ann a
            	left join iwill_ann_sign b on a.id = b.ann_id and b.s_no = '${params.s_no}'
            ) tbl where rown > ${params.start}
        """
        def rows = []
        sql.eachRow(s, []) {
            rows << it.toRowResult()
        }
        s = "select count(*) as total from iwill_ann"
        def total = sql.firstRow(s).total
        def result = [rows: rows, total: total]
        render result as JSON  
    }

    def pos1_ann_log() {
        header( "Access-Control-Allow-Origin", "*" )

        def sql = new Sql(dataSource)
        def s = """
            insert into iwill_ann_log(version, ann_id, ip, date_created, last_updated) values(0, ?, ?, GETDATE(), GETDATE())
        """
        sql.execute(s, [params.annId, request.remoteAddr])
        def result = [status: 'okay']
        render result as JSON
    }

    def pos1_sign() {
        header( "Access-Control-Allow-Origin", "*" )

        def result = [:]
        def sql = new Sql(dataSource)
        def s = 'select OP_NAME from CASHIER where OP_NO = ? and OP_PASSWORD = ?'
        def row = sql.firstRow(s, [params.workno, params.password])
        if (row) {
            s = """
                insert into iwill_ann_sign(version, ann_id, s_no, op_no, op_name, date_created, last_updated) values(0, ?, ?, ?, ?, GETDATE(), GETDATE());
                SELECT * FROM iwill_ann_sign WHERE id = SCOPE_IDENTITY();
            """
            row = sql.firstRow(s, [params.ann_id, params.s_no, params.workno, row.OP_NAME])
            result.op_name = row.op_name
            result.date_created = row.date_created
            result.status = 'okay'
        } else {
            result.status = 'invalid'
        }
        render result as JSON        
    }

    def pos2() {
        header( "Access-Control-Allow-Origin", "*" )
    
        def result = [:]
        def sql = new Sql(dataSource)
        result.rows = []
        sql.eachRow(_s.api_pos2, [params.s_no, params.the_day]) {
            result.rows << it.toRowResult()
        }
        def s = "select * from iwill_refund_sign where s_no = ? and refund_date = ?"
        result.metaData = sql.firstRow(s, [params.s_no, params.the_day.replace("/", "")]) ?: [:]
        render result as JSON
    }
    
    def pos2_sign() {
        header( "Access-Control-Allow-Origin", "*" )

        def result = [:]
        def sql = new Sql(dataSource)
        def s = 'select OP_NAME from CASHIER where OP_NO = ? and OP_PASSWORD = ?'
        def row = sql.firstRow(s, [params.workno, params.password])
        if (row) {
            s = """
                insert into iwill_refund_sign(version, date_created, last_updated, op_name, op_no, refund_date, s_no) values(0, GETDATE(), GETDATE(), ?, ?, ?, ?);
                SELECT * FROM iwill_refund_sign WHERE id = SCOPE_IDENTITY();
            """
            row = sql.firstRow(s, [row.OP_NAME, params.workno, params.the_day.replace("/", ""), params.s_no])
            result.op_name = row.op_name
            result.date_created = row.date_created
            result.status = 'okay'
        } else {
            result.status = 'invalid'
        }
        render result as JSON        
    }    

    def pos3() {
        header( "Access-Control-Allow-Origin", "*" )

        def data = []
        def sql = new Sql(dataSource)
        sql.eachRow(_s.api_pos3, [params.sdate, params.edate, params.s_no]) {
            data << it.toRowResult()
        } 
        render data as JSON
    }    

    def pos4() {
        header( "Access-Control-Allow-Origin", "*" )
        
        def data = []
        def y = params.sdate[0..3]
        def m = params.sdate[5..6]
        def sdate = "${y}/${m}/01"
        Calendar calendar = Calendar.instance
        calendar.set(y.toInteger(), m.toInteger() - 1, 1)
        def last = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        def edate = "${y}/${m}/${last}"
        def sql = new Sql(dataSource)
        sql.eachRow(_s.api_pos4, [sdate.toString(), edate.toString(), params.s_no]) {
            data << it.toRowResult()
        }
        render data as JSON
    }
}
