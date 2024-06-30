package org.javarush.dao;

import org.hibernate.SessionFactory;
import org.javarush.entity.Inventory;

public class InventoryDAO extends GenericDAO<Inventory> {

    public InventoryDAO(SessionFactory sessionFactory) {
        super(Inventory.class, sessionFactory);
    }
}
