package com.xc.apex.nre.customerdemo.model;

import java.util.List;

public class OrderBean {
    private String command; // 指令字符串
    private String total; // 订单总价
    private List<OrderItemBean> orderList; // 订单详情

    public OrderBean(String command, String total, List<OrderItemBean> orderList) {
        this.command = command;
        this.total = total;
        this.orderList = orderList;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<OrderItemBean> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderItemBean> orderList) {
        this.orderList = orderList;
    }

    @Override
    public String toString() {
        return "OrderDataBean{" +
                "command='" + command + '\'' +
                ", total='" + total + '\'' +
                ", orderList=" + orderList +
                '}';
    }
}
