package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);//计算指定日期的后一天对应的日期
            dateList.add(begin);
        }

        //封装VO要求的格式
        String dateList2Str = StringUtils.join(dateList, ",");

        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList){
            //査询date日期对应的营业数据，营业额是指:状态为“已完成”的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//获取指定日期的00:00:00
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//获取指定日期的23:59:59

            //select sum(amount) from orders where order_time > beginTime and order_time < endTime and status = 5
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnoverList.add(turnover == null ? 0.0 : turnover);
        }

        //封装VO要求的格式
        String turnoverList2Str = StringUtils.join(turnoverList, ",");

        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(dateList2Str);
        turnoverReportVO.setTurnoverList(turnoverList2Str);

        return turnoverReportVO;
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);//计算指定日期的后一天对应的日期
            dateList.add(begin);
        }

        //封装VO要求的格式
        String dateList2Str = StringUtils.join(dateList, ",");

        //存放每天的新增用户数量 select count(id) from user where create_time > beginTime and create_time < endTime
        List<Integer> newUserList = new ArrayList<>();
        //存放每天的总用户数量 select count(id) from user where create_time < endTime
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);

            //当日总用户数量
            Integer totalUser = userMapper.countByMap(map);

            map.put("begin", beginTime);
            //当日新增用户数量
            Integer newUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        String totalUserList2Str = StringUtils.join(totalUserList, ",");
        String newUserList2Str = StringUtils.join(newUserList, ",");

        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(dateList2Str);
        userReportVO.setNewUserList(newUserList2Str);
        userReportVO.setTotalUserList(totalUserList2Str);

        return userReportVO;
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);//计算指定日期的后一天对应的日期
            dateList.add(begin);
        }

        //封装VO要求的格式
        String dateList2Str = StringUtils.join(dateList, ",");

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        //查询每天的有效订单数和订单总数
        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询每天的订单总数 select count(id) from orders where order_time > beginTime and order_time < endTime
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            //查询每天的有效订单数 select count(id) from orders where order_time > beginTime and order_time < endTime and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        String orderCountList2Str = StringUtils.join(orderCountList, ",");
        String validOrderCountList2Str = StringUtils.join(validOrderCountList, ",");

        Integer totalOrderCount = orderCountList.stream().mapToInt(x -> x).sum();
        Integer validOrderCount = validOrderCountList.stream().mapToInt(x -> x).sum();
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0){
            orderCompletionRate = validOrderCountList.stream().mapToInt(x -> x).sum() * 1.0 / orderCountList.stream().mapToInt(x -> x).sum();
        }

        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(dateList2Str);
        orderReportVO.setOrderCountList(orderCountList2Str);
        orderReportVO.setValidOrderCountList(validOrderCountList2Str);
        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setValidOrderCount(validOrderCount);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);

        return orderReportVO;
    }

    /**
     * 销量排名top10
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        for (GoodsSalesDTO dto : salesTop10){
            nameList.add(dto.getName());
            numberList.add(dto.getNumber());
        }

        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(StringUtils.join(nameList, ","));
        salesTop10ReportVO.setNumberList(StringUtils.join(numberList, ","));

        return salesTop10ReportVO;
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }
}
