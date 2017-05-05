package iext

import java.text.*
import grails.util.GrailsUtil
import java.util.UUID
import groovy.io.FileType
import groovy.json.*

import me.chanjar.weixin.common.util.BeanUtils
import java.util.HashMap
import java.util.Map
import java.util.Set
import javax.servlet.http.HttpServletRequest

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

class _ {

    static obj2map(Object obj) {
        def map = [:]
        obj.class.declaredFields.findAll { !it.synthetic }.each {
            if (it.name != 'serialVersionUID') {
                map[it.name] = obj[it.name]
            }
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
        return 'wx13f69830d69c3748' // dev() ? 'wx150d0099b302cee6' : 'wx13f69830d69c3748'
    }

    static getWxmpAppSecret() {
        return '48fcca0f2f720679653c65ffde917bbe' // dev() ? '0bcd616e16fa378bb38ae54f1252c567' : '48fcca0f2f720679653c65ffde917bbe'
    }

    static getWxmpToken() {
        return 'abcde12345' // dev() ? 'SECRET_WORD' : 'abcde12345'
    }

    static getWxmpAesKey() {
        return dev() ? 'YTbTlbrOxbPl64fbDMBi8gs7TqAkvd42Bd0RRVwuNh1' : 'YTbTlbrOxbPl64fbDMBi8gs7TqAkvd42Bd0RRVwuNh1'
    }

    static getWxmpMchId() {
        return '1364772002' // dev() ? '1364772002' : '1364772002'
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


    static zh2py(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (_t != null) {
                        pybf.append(_t[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }     

    static canUseInPda(str) {
        boolean foo = false
        int count = 0
        str.each {
            if (Integer.toHexString((int) it.charAt(0)).length() == 4) {
                count += 2
            } else {
                count += 1
            }            
            if (count == 22) { // 要刚好 22 不然就是中文字会拆开来
                foo = true
            }
        }
        if (count < 22) { // 小于 22 的也没事
            foo = true
        }
        return foo
    }

    static _STORE_STATUS = ['1': '正常', '2': '已停業']
    static _PART_STATUS = ['1': '正常', '3': '删除']    
}

