package org.javarush.dao;

import org.hibernate.SessionFactory;
import org.javarush.entity.Actor;

public class ActorDAO extends GenericDAO<Actor> {
    public ActorDAO(SessionFactory sessionFactory) {
        super(Actor.class, sessionFactory);
    }
}
