package org.javarush.dao;

import org.hibernate.SessionFactory;
import org.javarush.entity.Store;

public class StoreDAO extends GenericDAO<Store> {
    public StoreDAO(SessionFactory sessionFactory) {
        super(Store.class, sessionFactory);
    }
}
