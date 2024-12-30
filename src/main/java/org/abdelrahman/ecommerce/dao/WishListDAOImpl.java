package org.abdelrahman.ecommerce.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.abdelrahman.ecommerce.entity.WishList;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public class WishListDAOImpl implements WishListDAO {
    private final EntityManager em;

    WishListDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    @Override
    public void addToWishList(WishList wishListDAO) {
        em.persist(wishListDAO);
    }
    @Transactional
    @Override
    public void removeFromWishList(WishList wishList) {
        em.remove(wishList);
    }

    @Transactional
    @Override
    public List<WishList> getWishList() {
        return em.createQuery("select w from WishList w", WishList.class).getResultList();
    }

    @Transactional
    @Override
    public List<WishList> getWishListByCustomerId(int customerId) {
        TypedQuery<WishList> query = em.createQuery("select w from WishList w where w.user.id=:customerId", WishList.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @Transactional
    @Override
    public WishList getProductFromWishList(Long productId) {
        try {
            return em.createQuery("select w from WishList w where w.product.id = :productId", WishList.class)
                    .setParameter("productId", productId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null or handle it as needed
        }

    }
}
