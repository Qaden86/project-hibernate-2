package org.javarush.dao;

import org.hibernate.SessionFactory;
import org.javarush.entity.Language;

public class LanguageDAO extends GenericDAO<Language> {
    public LanguageDAO(SessionFactory sessionFactory) {
        super(Language.class, sessionFactory);
    }
}
