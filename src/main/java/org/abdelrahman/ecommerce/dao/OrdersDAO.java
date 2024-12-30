package org.abdelrahman.ecommerce.dao;

import org.abdelrahman.ecommerce.entity.Orders;

import java.util.List;

public interface OrdersDAO {
    void  addOrder(Orders order);
    void  deleteOrder(Orders order);
    List<Orders> getOrders();
    Orders getOrder(Long id);
}
