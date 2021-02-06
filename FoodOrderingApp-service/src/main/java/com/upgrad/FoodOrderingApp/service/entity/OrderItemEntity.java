package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@NamedQueries({

        @NamedQuery(name = "getOrderItemsByOrder",query = "SELECT o FROM OrderItemEntity o WHERE o.orders = :orders ORDER BY o.item.itemName ASC"),
        @NamedQuery(name = "getItemsByOrders",query = "SELECT o FROM OrderItemEntity o WHERE o.orders = :ordersEntity"),

})
public class OrderItemEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity orders;

    @ManyToOne
    @JoinColumn(name = "item_id" , referencedColumnName = "id")
    private ItemEntity item;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name="PRICE")
    private Integer price;
    public OrderItemEntity(){}
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OrderEntity getOrders() {
        return orders;
    }

    public void setOrders(OrderEntity orders) {
        this.orders = orders;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
