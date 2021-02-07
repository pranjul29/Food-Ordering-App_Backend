package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "category_item")
@NamedQueries({
  @NamedQuery(
      name = "getItemsByCategory",
      query =
          "SELECT c FROM CategoryItemEntity c WHERE c.category = :category ORDER BY LOWER(c.item.itemName) ASC "),
})
public class CategoryItemEntity {
  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "item_id")
  private ItemEntity item;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public ItemEntity getItem() {
    return item;
  }

  public void setItem(ItemEntity item) {
    this.item = item;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public void setCategory(CategoryEntity category) {
    this.category = category;
  }
}
