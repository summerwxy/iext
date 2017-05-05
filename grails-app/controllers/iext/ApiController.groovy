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


    def q6l() {
        header( "Access-Control-Allow-Origin", "*" )
        
        def data = []
        def sdate = params.sdate[0..3] + params.sdate[5..6] + params.sdate[8..9]
        def edate = params.edate[0..3] + params.edate[5..6] + params.edate[8..9]
        def sql = new Sql(dataSource)
        sql.eachRow(_s.api_q6l, [sdate: sdate, edate: edate, s_no: params.s_no, p_no: params.p_no]) {
            data << it.toRowResult()
        }
        render data as JSON   
    }

    def q1v_1() {
        header( "Access-Control-Allow-Origin", "*" )
        def result = []
        def sql = new Sql(dataSource)
        sql.eachRow(_s.api_q1v_1, []) {
            result << it.toRowResult()
        }
        render result as JSON
    }

    def q1v_2() {
        header( "Access-Control-Allow-Origin", "*" )
        def result = []
        def sql = new Sql(dataSource)
        sql.eachRow(_s.api_q1v_2, [params.d_no]) {
            result << it.toRowResult()
        }
        render result as JSON
    }

    def q1v_3() {
        header( "Access-Control-Allow-Origin", "*" )
        def result = []
        def sql = new Sql(dataSource)
        sql.eachRow(_s.api_q1v_3, [params.p_no]) {
            result << it.toRowResult()
        }
        render result as JSON
    }

    def qstore() {
        header( "Access-Control-Allow-Origin", "*" )    
        // 查詢
        def data = []
        def sql = new Sql(dataSource)
        def s = """
            select a.S_NO, a.S_NAME, a.S_TEL, a.S_IP, b.R_NAME, a.S_STATUS
            from STORE a
            left join REGION b on a.R_NO = b.R_NO
        """
        sql.eachRow(s, []) {
            def store = it.toRowResult()
            store.S_PY = _.zh2py(store.S_NAME)
            store.S_STATUS_NAME = _._STORE_STATUS[store.S_STATUS]
            store.label = store.S_NO + ' ' + store.S_NAME
            data << store
        }
        // 篩選
        def result = []
        if (params.q) {
            def kw = params.q.toLowerCase()
            data.each {
                if (it.S_PY.indexOf(kw) != -1 || it.S_TEL.indexOf(kw) != -1 || it.S_NO.indexOf(kw) != -1) {
                    result << it
                }
            } 
        } else {
            result = data
        }
        render result as JSON
    }

    def qpart() {
        header( "Access-Control-Allow-Origin", "*" )    
        // 查詢
        def data = []
        def sql = new Sql(dataSource)
        def s = """
            select a.P_NO, a.P_NAME, a.P_PRICE, a.UN_NO, b.D_CNAME, c.SUB_NAME, a.P_STATUS, P_SPMODE, P_NAME + P_SPMODE AS PDASTR
            from PART a 
            left join DEPART b on a.D_NO = b.D_NO
            left join SUBDEP c on b.[GROUP] = c.SUBDEP
            order by P_NO
        """
        sql.eachRow(s, []) {
            def part = it.toRowResult()
            part.P_PY = _.zh2py(part.P_NAME).toLowerCase()
            part.P_STATUS_NAME = _._PART_STATUS[part.P_STATUS]
            part.P_PDA = _.canUseInPda(part.PDASTR) ? 'O' : 'X'
            part.label = part.P_NO + ' ' + part.P_NAME
            data << part
        }
        // 篩選
        def result = []
        if (params.w == 'part') {
            def kw = params.q.toLowerCase()
            data.each {
                if ((it.P_PY.indexOf(kw) != -1 || it.P_NAME.indexOf(kw) != -1 || it.P_NO.indexOf(kw) != -1) && result.size() <= 500) {
                    result << it
                }   
            }
        } else if (params.w == 'pda') {
            data.each {
                if (it.P_PDA == 'X' && it.P_STATUS == '1') {
                    result << it
                } 
            }
        }        
       
        render result as JSON
    }



}
