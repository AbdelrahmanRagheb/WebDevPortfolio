package org.abdelrahman.ecommerce.dao;

import jakarta.persistence.EntityManager;
import org.abdelrahman.ecommerce.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class OrdersDAOImpl implements OrdersDAO {

    private final EntityManager em;

    @Autowired
    OrdersDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    @Override
    public void addOrder(Orders orders) {
        em.persist(orders);
    }
    @Transactional
    @Override
    public void deleteOrder(Orders order) {
        em.remove(order);
    }
    @Transactional
    @Override
    public List<Orders> getOrders() {
        return em.createQuery("from Orders", Orders.class).getResultList();
    }
    @Transactional
    @Override
    public Orders getOrder(Long id) {
        return em.find(Orders.class, id);
    }
}
