package iext

import java.text.*
import grails.util.GrailsUtil
import java.util.UUID
import groovy.io.FileType
import groovy.json.*

import java.util.HashMap
import java.util.Map
import java.util.Set
import javax.servlet.http.HttpServletRequest

class _ {

    static obj2map(Object obj) {
        def map = [:]
        obj.class.declaredFields.findAll { !it.synthetic }.each {
            map[it.name] = obj[it.name]
        }
        return map
    }

    static date2String(Date d, format='yyyyMMdd') {
        SimpleDateFormat sdf = new SimpleDateFormat(format)
        return sdf.format(d)
    }

    static string2Date(String s, format='yyyyMMdd') {
        SimpleDateFormat sdf = new SimpleDateFormat(format)
        return sdf.parse(s)
    }

    static dateStringRemoveSlash(String s) {
        return date2String(string2Date(s, 'yyyy/MM/dd'))
    }

    static dateStringAddSlash(String s) {
        return date2String(string2Date(s), 'yyyy/MM/dd')
    }

    static someday(field, amount, format) {
        def cal = Calendar.getInstance()
        cal.add(field, amount)
        return date2String(cal.getTime(), format)
    }

    static diffDays(date1, date2, format='yyyy/MM/dd') { // date1 - date2
        if (!date1 || !date2) {
            return 0
        }
        def date1Long = _.dateString2Long(date1, format)
        def date2Long = _.dateString2Long(date2, format)
        def foo = date1Long - date2Long
        return foo / (1000*60*60*24)
    }

    static today(format='yyyyMMdd') {
        return someday(Calendar.DATE, 0, format)
    }

    static yesterday(format='yyyyMMdd') {
        return someday(Calendar.DATE, -1, format)
    }

    static long2DateString(long lng, format='yyMMdd HHmm') {
        def cal = Calendar.getInstance()
        cal.setTimeInMillis(lng)
        return date2String(cal.getTime(), format)
    }

    static dateString2Long(String str, format='yyMMdd HHmm') {
        def d = string2Date(str, format)
        return d.getTime()
    }

    static formatDate(date, format='yyyy-MM-dd') {
        def sdf = new SimpleDateFormat(format)
        return sdf.format(date) 
    }

    static numberFormat(num, pattern='##,##0.0') {
        def df = new DecimalFormat(pattern)
        return df.format(num)
    }

    static uuid() {
        return UUID.randomUUID() as String
    }


    static wxOauth2Url(c, a, p=[]) {
        def url = ''
        if(GrailsUtil.getEnvironment() == "development") {
            url = "http://test.dsiwill.com/${c}/${a}"
        } else if (GrailsUtil.getEnvironment() == "production") {
            url = "http://api.dsiwill.com/iext/${c}/${a}"
        } else {
            throw new Exception()
        }
        if (p) {
            def foo = []
            p.each {
                foo << it.key + '=' + it.value
            }
            url += '?' + foo.join('&')
        }
        return url
    }

    public static Map<String, String> getRequestParams(HttpServletRequest request){
        
        Map<String, String> params = new HashMap<String, String>();
        if(null != request){
            Set<String> paramsKey = request.getParameterMap().keySet();
            for(String key : paramsKey){
                params.put(key, request.getParameter(key));
            }
        }
        return params;
    }

    static dev() {
        boolean result = false
        if(GrailsUtil.getEnvironment() == "development") {
            result = true
        } else if (GrailsUtil.getEnvironment() == "production") {
            result = false
        } else {
            throw new Exception()
        }
        return result
    }

    static getWxmpAppId() {
        return dev() ? 'wxb3235f51f36fe4c8' : 'wx13f69830d69c3748'
    }

    static getWxmpAppSecret() {
        return dev() ? '7a879230c9c931338df16675a58df2fc' : '48fcca0f2f720679653c65ffde917bbe'
    }

    static getWxmpToken() {
        return dev() ? 'abcde12345' : 'abcde12345'
    }

    static getWxmpAesKey() {
        return dev() ? 'YTbTlbrOxbPl64fbDMBi8gs7TqAkvd42Bd0RRVwuNh1' : 'YTbTlbrOxbPl64fbDMBi8gs7TqAkvd42Bd0RRVwuNh1'
    }

    static getWxmpMchId() {
        return dev() ? '1364772002' : '1364772002'
    }

    static getWxmpMchKey() {
        return dev() ? 'b84b9bb08bd8f064fab58420c7d304bb' : 'b84b9bb08bd8f064fab58420c7d304bb'
    }


    // TODO: test it
    // 因為卡再 Grails 預設 groovy 版本問題, 開發環境與正式環境處理方式不一樣
    static parseJson(url) {
        def result = null
        def slurper = new JsonSlurper()
        if (_.dev()) {
            // result = slurper.parseText(url.toURL().text) // Grails 2.X 的時候有區別特地寫的, 可能跟 web container 有關係
            result = slurper.parse(new URL(url), 'utf-8')
        } else {            
            result = slurper.parse(new URL(url), 'utf-8')
        }    
        return result
    }

}

