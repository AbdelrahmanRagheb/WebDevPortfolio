package org.abdelrahman.ecommerce.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "username",unique = true)
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "address")
    private String address;
    @Column(name = "account_type")
    private String accountType;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Product> products;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER)
    @JoinTable(name = "product_like",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<WishList> wishlist;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Orders> orders;


    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getLikes() {
        return likes;
    }

    public void setLikes(List<Product> likes) {
        this.likes = likes;
    }

    public User() {
    }

    public User(String fullName, String username, String password, String email, String address, String accountType) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.address = address;
        this.accountType = accountType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public void addProduct(Product product) {
        if (products == null) {
            products = new ArrayList<Product>();
        }
        product.setUser(this);
        this.setAccountType("ROLE_VENDOR");
        products.add(product);
    }

    public void addProductToWishList(WishList product) {
        if (wishlist == null) {
            wishlist = new ArrayList<>();
        }
        wishlist.add(product);
       product.setUser(this);

    }

    public void removeProductFromWishList(WishList product) {
        if (wishlist != null) {
            wishlist.remove(product);
        }

    }

    public void addProductPurchase(Orders product) {
        if (orders == null) {
            orders = new ArrayList<>();
        }
        product.setUser(this);
        orders.add(product);

    }


    public void addProductLike(Product product) {
        if (likes == null) {
            likes = new ArrayList<Product>();
        }
        likes.add(product);
    }


    public List<WishList> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<WishList> wishlist) {
        this.wishlist = wishlist;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", accountType='" + accountType + '\'' +
                '}';
    }

}
