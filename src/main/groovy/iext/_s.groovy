package iext


class _s {

    static api_pos2 = """
        declare @store varchar(8)
        declare @day0 varchar(10)
        declare @day_1 varchar(10)
        set @store = ?
        set @day0 = CONVERT(varchar, CONVERT(datetime, ?), 112)
        set @day_1 = CONVERT(varchar, DATEADD(d, -1, CONVERT(datetime, @day0)), 112)
        -- 订货
        select a.P_NO, SUM(a.P_QTY) AS PO_QTY into #aa
        from PO_D a left join POR b on a.POR_NO = b.POR_NO and a.S_NO = b.S_NO
        where b.FLS_NO = 'AP' and b.S_NO = @store and b.POR_DATE = @day_1 and a.P_QTY <> 0 group by a.P_NO
        -- 进货
        select a.P_NO, SUM(a.P_QTY) as IN_QTY into #bb
        from INS_D a left join INS_H b on a.IN_NO = b.IN_NO and a.S_NO = b.S_NO
        where b.FLS_NO = 'CO' and b.S_NO = @store and b.UPD_DATE = @day0 group by a.P_NO
        -- 生產入庫
        select a.P_NO, SUM(a.SST_QTY) as SST_QTY into #cc
        from S_STOR_D a left join S_STOR_H b on a.S_NO = b.S_NO and a.SST_NO = b.SST_NO
        where b.FLS_NO = 'CO' and a.S_NO = @store and b.SPK_DATE = @day0 group by a.P_NO
        -- 撥入
        select a.P_NO, SUM(a.TR_OUT_QTY) as TR_OUT_QTY into #dd
        from TRAN_D a left join TRAN_H b on a.TR_NO = b.TR_NO and a.S_NO_OUT = b.S_NO_OUT
        where b.FLS_NO in ('CF', 'CO') and b.S_NO_IN = @store and b.TR_DATE = @day0 group by a.P_NO
        -- 撥出
        select a.P_NO, SUM(a.TR_IN_QTY) as TR_IN_QTY into #ee
        from TRAN_D a left join TRAN_H b on a.TR_NO = b.TR_NO and a.S_NO_OUT = b.S_NO_OUT
        where b.FLS_NO in ('CF', 'CO') and b.S_NO_OUT = @store and b.TR_DATE = @day0 group by a.P_NO
        -- 退貨
        select a.P_NO, SUM(a.P_QTY) as BA_QTY into #ff
        from BACK_D a left join BACK_H b on a.S_NO = b.S_NO and a.BA_NO = b.BA_NO
        where b.FLS_NO in ('CF', 'CO') and a.BA_REASON in ('A01') and a.S_NO = @store and b.BA_DATE = @day0 group by a.P_NO
        -- 銷售
        select P_NO, SUM(SL_QTY) as SL_QTY into #gg
        from SALE_D 
        where S_NO = @store and SL_DATE = @day0 group by P_NO
        -- join all
        select p.P_NO, p.P_NAME, isnull(a.PO_QTY, 0) as PO_QTY, isnull(b.IN_QTY, 0) as IN_QTY, ISNULL(c.SST_QTY, 0) as SST_QTY, isnull(d.TR_OUT_QTY, 0) as TR_OUT_QTY, isnull(e.TR_IN_QTY, 0) as TR_IN_QTY, isnull(f.BA_QTY, 0) as BA_QTY, isnull(g.SL_QTY, 0) as SL_QTY, isnull(h.PS_QTY, 0) as PS_QTY into #hh
        from part p left join #aa a on p.P_NO = a.P_NO left join #bb b on p.P_NO = b.P_NO left join #cc c on p.P_NO = c.P_NO left join #dd d on p.P_NO = d.P_NO left join #ee e on p.P_NO = e.P_NO left join #ff f on p.P_NO = f.P_NO left join #gg g on p.P_NO = g.P_NO 
        left join IWILL_PART_S h on h.S_NO = @store and h.PS_DATE = @day0 and p.P_NO = h.P_NO
        where p.P_PRICE > 0 and not (a.PO_QTY is null and b.IN_QTY is null and c.SST_QTY is null and d.TR_OUT_QTY is null and e.TR_IN_QTY is null and f.BA_QTY is null and g.SL_QTY is null)
        -- 只找有退货的
        select *, case IN_QTY + SST_QTY when 0 then null else BA_QTY / (IN_QTY + SST_QTY) * 100 end as BA_PRCNT
        from #hh where BA_QTY > 0 
        order by P_NO
    """


    static api_pos3 = """
        declare @pos_dates varchar(12)
        declare @pos_datee varchar(12)
        declare @store varchar(100)
        set @pos_dates = convert(varchar(12), convert(datetime, ?), 112)
        set @pos_datee = convert(varchar(12), convert(datetime, ?), 112) 
        set @store = ?
        SELECT S_NO, S_NAME INTO #aa FROM STORE WHERE S_NO IN (@store)    
        select P_NO, P_NAME, case when D_NO in ('1611', '1612', '1613', '1614', '1615') then 'A' -- '1. 面包'
        when D_NO ='1616' then 'B' -- '2. 蛋糕'
        when D_NO in ('1617') then 'C' -- '3. 冷点'
        when D_NO in ('1619', '1620') then 'D' -- '4. 西点'
        when D_NO in ('1801', '1802', '1803', '1804', '1805', '1806', '1807', '1808') then 'E' -- '5. 现烤'
        else 'XX' end as category
        into #a from part 
        select * into #bb from #a where category <> 'XX'
        SELECT b.S_NO, a.P_NO, SUM(a.P_QTY) AS IN_QTY, SUM(a.P_AMT) AS IN_AMT
        INTO #cc
        FROM INS_D a
        LEFt JOIN INS_H b ON a.IN_NO = b.IN_NO AND a.S_NO = b.S_NO
        WHERE b.S_NO in (@store) AND a.P_NO IN (SELECT P_NO FROM #bb)
        AND b.UPD_DATE >= @pos_dates AND b.UPD_DATE <= @pos_datee
        AND b.FLS_NO IN ('CO')
        GROUP BY b.S_NO, a.P_NO
        SELECT b.S_NO, a.P_NO, SUM(a.P_QTY) AS BA_QTY, SUM(a.P_AMT) AS BA_AMT, SUM(a.QTY_BAT) AS BA_QTY1
        INTO #dd
        FROM BACK_D a
        LEFt JOIN BACK_H b ON a.BA_NO = b.BA_NO AND a.S_NO = b.S_NO
        WHERE  b.S_NO in (@store) AND a.P_NO IN (SELECT P_NO FROM #bb)
        AND b.BA_DATE >= @pos_dates AND b.BA_DATE <= @pos_datee
        AND b.FLS_NO IN ('CF') AND a.BA_REASON in ('A01') -- 产品到期
        GROUP BY b.S_NO, a.P_NO
        SELECT b.S_NO, a.P_NO, SUM(a.SL_QTY) AS SL_QTY, SUM(a.SL_AMT) AS SL_AMT
        INTO #ee
        FROM SALE_D a
        LEFt JOIN SALE_H b ON a.SL_KEY = b.SL_KEY
        WHERE  b.S_NO in (@store) AND a.P_NO IN (SELECT P_NO FROM #bb)
        AND b.SL_DATE >= @pos_dates AND b.SL_DATE <= @pos_datee
        GROUP BY b.S_NO, a.P_NO
        select a.S_NO, a.P_NO, SUM(a.SST_QTY) as SST_QTY, SUM(a.SST_QTY * c.P_PRICE) as SST_AMT
        into #ff
        from S_STOR_D a 
        left join S_STOR_H b on a.SST_NO = b.SST_NO and a.S_NO = b.S_NO
        left join part c on a.P_NO = c.P_NO
        where b.S_NO in (@store) and a.P_NO in (select P_NO from #bb) 
        and b.SPK_DATE >= @pos_dates and b.SPK_DATE <= @pos_datee
        group by a.S_NO, a.P_NO
        select b.S_NO_IN as S_NO, a.P_NO, sum(a.tr_out_qty) as tr_in_qty, SUM(a.tr_out_qty * c.P_PRICE) as TR_in_AMT
        into #gg
        from TRAN_D a
        left join TRAN_H b on a.TR_NO = b.TR_NO and a.S_NO_OUT = b.S_NO_OUT
        left join PART c on a.P_NO = c.P_NO
        where b.FLS_NO in ('CF', 'CO') and b.S_NO_IN in (@store) and a.P_NO in (select P_NO from #bb) and b.TR_DATE >= @pos_dates and b.TR_DATE <= @pos_datee
        group by b.S_NO_IN, a.P_NO
        select b.S_NO_OUT as S_NO, a.P_NO, sum(a.tr_out_qty) as tr_out_qty, SUM(a.tr_out_qty * c.P_PRICE) as tr_out_amt
        into #hh
        from TRAN_D a
        left join TRAN_H b on a.TR_NO = b.TR_NO and a.S_NO_OUT = b.S_NO_OUT
        left join PART c on a.P_NO = c.P_NO
        where b.FLS_NO in ('CF', 'CO') and b.S_NO_OUT in (@store) and a.P_NO in (select P_NO from #bb) and b.TR_DATE >= @pos_dates and b.TR_DATE <= @pos_datee
        group by b.S_NO_OUT, a.P_NO
        SELECT a.S_NO + b.P_NO as id, a.S_NO, a.S_NAME, b.category, b.P_NO, b.P_NAME
        , isnull(c.IN_QTY, 0) AS in_qty, isnull(c.IN_AMT, 0) AS in_amt
        , isnull(f.SST_QTY, 0) as sst_qty, isnull(f.SST_AMT, 0) as sst_amt
        , isnull(d.BA_QTY, 0) AS ba_qty, isnull(d.BA_QTY1, 0) AS ba_qty1, isnull(d.BA_AMT, 0) AS ba_amt
        , isnull(e.SL_QTY, 0) AS sl_qty, isnull(e.SL_AMT, 0) AS sl_amt
        , isnull(g.tr_in_qty, 0) as tr_in_qty, isnull(g.tr_in_amt, 0) as tr_in_amt
        , isnull(h.tr_out_qty, 0) as tr_out_qty, isnull(h.tr_out_amt, 0) as tr_out_amt
        , (case when isnull(d.BA_QTY, 0) = 0 then 0 when (isnull(c.IN_QTY, 0)+isnull(f.SST_QTY, 0)+isnull(g.tr_in_qty, 0)-isnull(h.tr_out_qty, 0)) = 0 then 999999 else isnull(d.BA_QTY, 0) / (isnull(c.IN_QTY, 0)+isnull(f.SST_QTY, 0)+isnull(g.tr_in_qty, 0)-isnull(h.tr_out_qty, 0)) end) * 100 as back_rate
        FROM #aa a 
        FULL JOIN #bb b ON 1 = 1
        LEFT JOIN #cc c ON a.S_NO = c.S_NO AND b.P_NO = c.P_NO
        LEFT JOIN #dd d ON a.S_NO = d.S_NO AND b.P_NO = d.P_NO
        LEFT JOIN #ee e ON a.S_NO = e.S_NO AND b.P_NO = e.P_NO
        left join #ff f ON a.S_NO = f.S_NO and b.P_NO = f.P_NO
        left join #gg g ON a.S_NO = g.S_NO and b.P_NO = g.P_NO
        left join #hh h ON a.S_NO = h.S_NO and b.P_NO = h.P_NO
        WHERE NOT (c.P_NO IS NULL AND d.P_NO IS NULL AND e.P_NO IS NULL)
        --and b.category in ('A', 'B')
        ORDER BY a.S_NAME, b.category, e.SL_AMT desc
    """


    // 0083
    static api_pos4 = """
        declare @p_dates varchar(20)
        declare @p_datee varchar(20)
        declare @p_store varchar(20)
        set @p_dates = ? -- '20160227'
        set @p_datee = ? -- '20160301'
        set @p_store= ? -- '8022015'

        -- =============== START ==================
        declare @dates varchar(8) 
        declare @datee varchar(8) 
        set @dates = convert(char, convert(datetime, @p_dates), 112) -- 日期
        set @datee = convert(char, convert(datetime, @p_datee), 112) -- 日期

        ---充值----
        select  GI_DATE,GI_BILL_SNO as S_NO, SUM(GI_AMT) as 充值
        into #充值
        from GIFT_LIFE  where GI_TYPE ='ADD'   and GI_BILL_SNO<>'0000000' and REMARK1='' 
        and GI_DATE>=@dates and GI_DATE<=@datee  and  GI_BILL_SNO in(@p_store)
        group by GI_BILL_SNO,GI_DATE
        

        SELECT  SL_DATE,a.S_NO, b.S_NAME,b.r_no, count(SL_KEY) as 总客流量,COUNT(distinct a.SL_DATE) as 营业天数,
        sum(isnull(SL_AMT,0)) as 总价, sum(isnull(SL_DISC_AMT,0)) as 折扣, sum(isnull(PAY_AMT,0)) as 营业总额, sum(isnull(PAY_CASH,0)) as 现金, --status_C 状态
        sum(isnull(PAY_CARD,0)) as 非公司券, sum(isnull(PAY_3,0)) as 阳光卡, sum(isnull(PAY_4,0)) as 促销券, sum(isnull(PAY_5,0)) as 提货券, sum(isnull(PAY_6,0)) as 代金券, 
        sum(isnull(PAY_7,0)) as 代金券溢收, sum(isnull(PAY_8,0)) as 促销券溢收, sum(isnull(PAY_9,0)) as 旧阳光卡, sum(isnull(PAY_10,0)) as 银联卡, sum(isnull(PAY_11,0)) as 提货券溢收,
        suM(isnull(PAY_12,0)) as  挂账, suM(isnull(PAY_21,0)) as  支付宝, suM(isnull(PAY_23,0)) as  微信,
        suM( isnull(a.PAY_CASH,0)+isnull(a.PAY_10,0)+isnull(a.PAY_12,0)+isnull(a.PAY_21,0)+isnull(a.PAY_23,0)) as  现金H 
        INTO #tmp_Party全 
        FROM SALE_H a
        LEFT JOIN STORE b ON a.S_NO = b.S_NO
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee   and  a.S_NO in(@p_store)---  and a.S_NO='8022002'
        group by a.S_NO, b.S_NAME,b.r_no,SL_DATE
        order by a.S_NO
        SELECT  SL_DATE,a.S_NO, b.S_NAME, count(SL_KEY) as 总客流量,COUNT(distinct a.SL_DATE) as 营业天数,
        sum(isnull(SL_AMT,0)) as 总价, sum(isnull(SL_DISC_AMT,0)) as 折扣, sum(isnull(PAY_AMT,0)) as 营业总额, sum(isnull(PAY_CASH,0)) as 现金, --status_C 状态
        sum(isnull(PAY_CARD,0)) as 非公司券, sum(isnull(PAY_3,0)) as 阳光卡, sum(isnull(PAY_4,0)) as 促销券, sum(isnull(PAY_5,0)) as 提货券, sum(isnull(PAY_6,0)) as 代金券, 
        sum(isnull(PAY_7,0)) as 代金券溢收, sum(isnull(PAY_8,0)) as 促销券溢收, sum(isnull(PAY_9,0)) as 旧阳光卡, sum(isnull(PAY_10,0)) as 银联卡, sum(isnull(PAY_11,0)) as 提货券溢收,
        suM(isnull(PAY_12,0)) as  挂账  , suM(isnull(PAY_21,0)) as  支付宝 , suM(isnull(PAY_23,0)) as  微信
        INTO #小于三千 
        FROM SALE_H a
        LEFT JOIN STORE b ON a.S_NO = b.S_NO
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee and (isnull(a.PAY_CASH,0)+isnull(a.PAY_10,0)+isnull(a.PAY_12,0)+isnull(a.PAY_21,0)+isnull(a.PAY_23,0))<3000  and  a.S_NO in(@p_store) and SL_SOURCE=2
        group by a.S_NO, b.S_NAME,SL_DATE
        order by a.S_NO

        SELECT  SL_DATE,a.S_NO, b.S_NAME, count(SL_KEY) as 总客流量,COUNT(distinct a.SL_DATE) as 营业天数,
        suM( isnull(a.PAY_CASH,0)+isnull(a.PAY_10,0)+isnull(a.PAY_12,0)+isnull(a.PAY_21,0)+isnull(a.PAY_23,0)) as  现金H, 
        sum(isnull(PAY_AMT,0)) as 营业总额,

        sum(isnull(PAY_CARD,0)) as 非公司券, sum(isnull(PAY_3,0)) as 阳光卡, sum(isnull(PAY_4,0)) as 促销券, sum(isnull(PAY_5,0)) as 提货券, sum(isnull(PAY_6,0)) as 代金券, 
        sum(isnull(PAY_7,0)) as 代金券溢收, sum(isnull(PAY_8,0)) as 促销券溢收, sum(isnull(PAY_9,0)) as 旧阳光卡, sum(isnull(PAY_10,0)) as 银联卡, sum(isnull(PAY_11,0)) as 提货券溢收,
        suM(isnull(PAY_12,0)) as  挂账  , suM(isnull(PAY_21,0)) as  支付宝 , suM(isnull(PAY_23,0)) as  微信
        INTO #大于三千
        FROM SALE_H a
        LEFT JOIN STORE b ON a.S_NO = b.S_NO
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee   and isnull(a.PAY_CASH,0)+isnull(a.PAY_10,0)+isnull(a.PAY_12,0)+isnull(a.PAY_21,0)+isnull(a.PAY_23,0)>=3000 and   SL_AMT >=3000  and  a.S_NO in(@p_store) and SL_SOURCE=2
        group by a.S_NO, b.S_NAME,SL_DATE
        order by a.S_NO
        ----本月客取非本月客订
        SELECT  c.SL_DATE,a.S_NO, b.S_NAME, count(a.SL_KEY) as 总客流量,COUNT(distinct a.SL_DATE) as 营业天数,
        sum(isnull(a.SL_AMT,0)) as 总价, sum(isnull(a.SL_DISC_AMT,0)) as 折扣, sum(isnull(a.PAY_AMT,0)) as 营业总额, sum(isnull(a.PAY_CASH,0)) as 现金, --status_C 状态
        sum(isnull(a.PAY_CARD,0)) as 非公司券, sum(isnull(a.PAY_3,0)) as 阳光卡, sum(isnull(a.PAY_4,0)) as 促销券, sum(isnull(a.PAY_5,0)) as 提货券, sum(isnull(a.PAY_6,0)) as 代金券, 
        sum(isnull(a.PAY_7,0)) as 代金券溢收, sum(isnull(a.PAY_8,0)) as 促销券溢收, sum(isnull(a.PAY_9,0)) as 旧阳光卡, sum(isnull(a.PAY_10,0)) as 银联卡, sum(isnull(a.PAY_11,0)) as 提货券溢收,
        suM(isnull(a.PAY_12,0)) as  挂账  , suM(isnull(a.PAY_21,0)) as  支付宝 , suM(isnull(a.PAY_23,0)) as  微信
        INTO #本月取
        FROM SALE_H a
        LEFT JOIN STORE b ON a.S_NO = b.S_NO
        left join SALE_ORDER_H c on a.SL_KEY_ORDER=c.SL_KEY
        WHERE a.SL_DATE >= @dates AND a.SL_DATE <= @datee  and a.SL_SOURCE=2  and  a.S_NO in(@p_store)
        and c.SL_DATE<@dates
        group by a.S_NO, b.S_NAME,c.SL_DATE
        order by a.S_NO

        SELECT SL_DATE,S_NO, SUM(isnull(AMT,0)) as 非公司券礼券S into #非公司券礼券S---相当于券
        FROM SALE_CARD_PAY 
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee  and rtrim(ltrim(CARD_NAME)) in  (select  FUNC_NAME from [iwill].[dbo].[STORE_CARD_Temp] where FUNC=2) and  S_NO in(@p_store)
        group by S_NO,SL_DATE

        SELECT SL_DATE,S_NO, SUM(isnull(AMT,0)) as 非公司券业绩S into #非公司券业绩S---相当于现金
        FROM SALE_CARD_PAY 
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee  and SL_AMT<3000  and rtrim(ltrim(CARD_NAME))  in  (select  FUNC_NAME from [iwill].[dbo].[STORE_CARD_Temp] where FUNC=1)  and  S_NO in(@p_store)
        group by SL_DATE,S_NO

        SELECT SL_DATE,S_NO, SUM(isnull(AMT,0)) as 非公司券促销券S into #非公司券促销券S---相当于促销券
        FROM SALE_CARD_PAY 
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee  and rtrim(ltrim(CARD_NAME))  in  (select  FUNC_NAME from [iwill].[dbo].[STORE_CARD_Temp] where FUNC=3)  and  S_NO in(@p_store)
        group by SL_DATE,S_NO

        SELECT SL_DATE,S_NO, SUM(isnull(AMT,0)) as 非公司未分类S into #非公司未分类
        FROM SALE_CARD_PAY 
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee  and rtrim(ltrim(CARD_NAME))   in  (  select FUNC_NAME from  STORE_CARD where  GROUPID='BANK' and USERD='Y' and FUNC_NAME not in   (select  FUNC_NAME from [iwill].[dbo].[STORE_CARD_Temp]))  and  S_NO in(@p_store)
        group by SL_DATE, S_NO

        SELECT SL_DATE,S_NO, SUM(isnull(AMT,0)) as 非公司券月结券S into #非公司券月结券S
        FROM SALE_CARD_PAY  
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee   and SL_AMT<3000  and rtrim(ltrim(CARD_NAME))  in  ('月结券')  and  S_NO in(@p_store)
        group by SL_DATE, S_NO

        SELECT   c.SL_DATE,a.S_NO, SUM(isnull(AMT,0)) as 非公司业绩QS into #非公司业绩QS
        FROM SALE_CARD_PAY  a
        LEFT JOIN STORE b ON a.S_NO = b.S_NO 
        LEFT JOIN SALE_H H ON a.SL_KEY = H.SL_KEY 
        left join SALE_ORDER_H c on H.SL_KEY_ORDER=c.SL_KEY
        WHERE H.SL_DATE >= @dates AND H.SL_DATE <= @datee  and a.SL_AMT<3000 and rtrim(ltrim(CARD_NAME)) in  (select  FUNC_NAME from [iwill].[dbo].[STORE_CARD_Temp] where FUNC=1)
        and c.SL_DATE<@dates    and  a.S_NO in(@p_store)
        group by c.SL_DATE,a.S_NO

        SELECT  H.SL_DATE,a.S_NO, SUM(isnull(AMT,0)) as 非公司月结券QS into #非公司月结券QS
        FROM SALE_CARD_PAY  a
        LEFT JOIN STORE b ON a.S_NO = b.S_NO       LEFT JOIN SALE_H H ON a.SL_KEY = H.SL_KEY 
        left join SALE_ORDER_H c on H.SL_KEY_ORDER=c.SL_KEY
        WHERE H.SL_DATE >= @dates AND H.SL_DATE <= @datee  and a.SL_AMT<3000 and rtrim(ltrim(CARD_NAME)) in  ('月结券')   and c.SL_DATE<@dates   and  a.S_NO in(@p_store) 
        group by a.S_NO,H.SL_DATE
        
        ---物料----
        select SL_DATE,S_NO, SUM(SL_AMT)as 节庆总额,sum(SL_TAXAMT) as 节庆营业总额
        into #节庆
        from SALE_D
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee and S_NO <> ''
        and ((DP_NO >= 8011 and DP_NO <= 8099) or (DP_NO >= 9011 and DP_NO <= 9099) or (DP_NO >= 9111 and DP_NO <= 9199))   and  S_NO in(@p_store)
        group by S_NO,SL_DATE

        select SL_DATE,S_NO, SUM(AMT)as 节庆提货总额
        into #节庆提货
        from SALE_CARD
        WHERE SL_DATE >= @dates AND SL_DATE <= @datee and S_NO <> ''
        and CARD_TYPE = 4 and CARD_NO like '9%'  and  S_NO in(@p_store)
        group by S_NO,SL_DATE

        select a.*, b.非公司券业绩S, c.非公司券促销券S, d.非公司券月结券S, e.非公司券礼券S, f.营业总额 as  小于三千, g.现金H as 大于三千, q.营业总额 as 本月取,
        n.充值, isnull(a.支付宝,0)+isnull(a.微信,0)+isnull(a.现金,0)+ISNULL(a.银联卡,0)+ISNULL(a.挂账,0)+isnull(b.非公司券业绩S,0)+ISNULL(n.充值,0) as 实际业绩,
        isnull(q.支付宝,0)+isnull(q.微信,0)+isnull(q.现金,0)+ISNULL(q.银联卡,0)+ISNULL(q.挂账,0)+isnull(MM.非公司业绩QS,0)+isnull(NN.非公司月结券QS,0) as 非本月实际业绩
        , isnull(OO.节庆总额, 0) as 节庆总额 , isnull(OO.节庆营业总额, 0) as 节庆营业总额, ISNULL(PP.节庆提货总额, 0) as 节庆提货总额,wp.非公司未分类S
        into #tmp_Party
        from #tmp_Party全 a 
        left join #非公司券业绩S b on a.S_NO=b.S_NO and a.SL_DATE=b.SL_DATE
        left join #非公司券促销券S c on a.S_NO=c.S_NO and a.SL_DATE=c.SL_DATE
        left join #非公司券月结券S d on a.S_NO=d.S_NO and a.SL_DATE=d.SL_DATE
        left join #非公司券礼券S e on a.S_NO=e.S_NO and a.SL_DATE=e.SL_DATE
        left join #小于三千 f on a.S_NO=f.S_NO and a.SL_DATE=f.SL_DATE
        left join #大于三千 g on a.S_NO=g.S_NO and a.SL_DATE=g.SL_DATE
        left join #充值 n on a.S_NO=n.S_NO and a.SL_DATE=n.GI_DATE
        left join #本月取 q on a.S_NO=q.S_NO and a.SL_DATE=q.SL_DATE
        left join #非公司业绩QS MM on a.S_NO=MM.S_NO and a.SL_DATE=MM.SL_DATE
        left join #非公司月结券QS NN on a.S_NO=NN.S_NO and a.SL_DATE=NN.SL_DATE
        left join #节庆 OO on a.S_NO = OO.S_NO and a.SL_DATE=OO.SL_DATE
        left join #节庆提货 PP on a.S_NO = PP.S_NO and a.SL_DATE=PP.SL_DATE
        left join #非公司未分类 wp on a.S_NO=wp.S_NO and a.SL_DATE=wp.SL_DATE

        select b.R_NO, b.R_NAME,SL_DATE, a.S_NO, S_NAME, 营业天数, (isnull(实际业绩,0)+ISNULL(非公司券月结券S,0)-ISNULL(大于三千,0))/营业天数 as 平均营业额, 营业总额 , isnull(代金券,0)+ISNULL(代金券溢收,0)+ISNULL(非公司券礼券S,0)+ISNULL(阳光卡,0) as 礼券回收,非公司未分类S
        , isnull(促销券,0)+ISNULL(非公司券促销券S,0) as 促销券, 折扣, isnull(大于三千,0) as 大于三千,  isnull(本月取,0) as 本月取, isnull(实际业绩,0)+ISNULL(非公司券月结券S,0) as 实际业绩订单, ISNULL(充值,0) as 充值,ISNULL(非公司券月结券S,0) as 非公司券月结券S
        ,isnull(实际业绩,0)+ISNULL(非公司券月结券S,0)-ISNULL(大于三千,0) as 实际业绩奖金,  ISNULL(非本月实际业绩,0) as 非本月实际业绩
        , 总客流量
        , case when 营业天数 = 0 then 0 else 总客流量/营业天数 end as 平均客流
        , case when 总客流量 = 0 then 0 else (isnull(实际业绩,0)+ISNULL(非公司券月结券S,0)-ISNULL(大于三千,0))/总客流量 end as 客单价, 提货券,提货券溢收
        ,节庆总额, 节庆提货总额,节庆营业总额, isnull(convert(decimal(18,2), c.day_target), 1) as 目标
        , case when c.day_target = 0 then 0 else convert(decimal(18,2),(isnull(实际业绩,0)+ISNULL(非公司券月结券S,0)-ISNULL(大于三千,0))/isnull(c.day_target,1)) end as 达成率
        from #tmp_Party a
        left join REGION b on a.R_NO=b.R_NO
        left join iwill_store_target c on a.S_NO=c.s_no and left(a.SL_DATE,6)=c.months
        where a.S_NO<>'8027010'
        order by b.R_NO,a.S_NO,SL_DATE
        
        drop table #tmp_Party,#tmp_Party全,#充值,#大于三千,#小于三千,#本月取,#节庆,#节庆提货,#非公司业绩QS,#非公司券业绩S,#非公司券促销券S,#非公司券月结券S,#非公司券礼券S,#非公司月结券QS,#非公司未分类
    """    

    static api_q1v_1 = """
        SELECT a.RECNO, a.D_NO, b.D_CNAME 
        FROM PART_MENU_D a
        LEFT JOIN DEPART b ON a.D_NO = b.D_NO
        WHERE a.MU_NO = '11MU000001' 
        ORDER BY a.RECNO
    """

    static api_q1v_2 = """
        SELECT a.RECNO, b.P_NO, b.P_NAME
        FROM PART_MENU_DD a
        LEFt JOIN PART b ON a.DD_NO = b.P_NO
        WHERE a.MU_NO = '11MU000001' AND a.D_NO = ?
        ORDER BY RECNO
    """

    static api_q1v_3 = """
        SELECT b.S_NAME, a.PS_QTY 
        FROM PART_S a
        LEFT JOIN STORE b ON a.S_NO = b.S_NO
        WHERE a.P_NO = ?
        AND a.PS_QTY <> 0
        ORDER BY b.S_NAME
    """

    static api_q6l = """
            select ps.S_NO, ps.P_NO, dbo.erosGetP_NAME(ps.P_NO) as P_NAME, ps.PS_QTY, lg.PSL_OLD_QTY, lg.PSL_CHG_QTY, lg.PSL_BILL_BNO
            , case lg.REMARK 
                when 'SALE' then '銷售单' 
                when 'INS' then '进货单' 
                when 'USELESS' then '库调单'
                when 'BACK' then '退货单' 
                when 'TRAN' then '调拨单' 
                when 'CO' then '盘点单'
                when 'PC' then '红利兑换'
                when 'UPDPNO' then '商品库存'
                when 'INS_BACK' then '进货回溯'
                when 'BAK_BACK' then '退货回溯' 
                when 'PA' then '组合拆解' 
                when 'SST' then '成品入库'
                when 'WP' then '分销出货' 
                when 'WB' then '分销退仓' 
                when 'SH' then '总仓出货'
                when 'SST_S' then '成品入库-扣原物料' 
                when 'ORDER' then '客订' 
                else dbo.erosGetMenu_Name(lg.REMARK) end REMARK, lg.PLS_DATE, lg.PLS_TIME
            from Part_s ps
            left join (select * from part_s_log lg 
                            where PLS_DATE >= :sdate AND PLS_DATE <= :edate AND (PSL_OLD_QTY <> 0 OR PSL_CHG_QTY <> 0)
                            AND P_NO = :p_no AND S_NO = :s_no)lg on lg.S_NO = ps.S_NO and lg.P_NO = ps.P_NO
            where ps.S_NO = :s_no AND ps.P_NO = :p_no
            order by ps.S_NO, ps.P_NO, lg.PLS_DATE, lg.PLS_TIME
    """
}

