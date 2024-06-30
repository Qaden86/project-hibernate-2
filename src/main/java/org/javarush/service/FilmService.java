package org.javarush.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javarush.dao.*;
import org.javarush.entity.*;
import org.javarush.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilmService {
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);
    private final SessionFactory sessionFactory;
    private final LanguageDAO languageDAO;
    private final CategoryDAO categoryDAO;
    private final ActorDAO actorDAO;
    private final FilmDAO filmDAO;
    private final FilmTextDAO filmTextDAO;

    public FilmService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.languageDAO = new LanguageDAO(sessionFactory);
        this.categoryDAO = new CategoryDAO(sessionFactory);
        this.actorDAO = new ActorDAO(sessionFactory);
        this.filmDAO = new FilmDAO(sessionFactory);
        this.filmTextDAO = new FilmTextDAO(sessionFactory);
    }

    public void newFilmWasMade() {
        Transaction transaction = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();

            Language language = languageDAO.getItems(0, 20)
                    .stream()
                    .unordered()
                    .findAny()
                    .orElseThrow(() -> new ServiceException("No language found"));

            List<Category> categories = categoryDAO.getItems(0, 5);
            if (categories.isEmpty()) {
                throw new ServiceException("No categories found");
            }

            List<Actor> actors = actorDAO.getItems(0, 20);
            if (actors.isEmpty()) {
                throw new ServiceException("No actors found");
            }

            Film film = new Film();
            film.setLanguage(language);
            film.setActors(new HashSet<>(actors));
            film.setRating(Rating.NC17);
            film.setSpecialFeature(Set.of(Feature.TRAILERS, Feature.COMMENTARIES));
            film.setLength((short) 55);
            film.setReplacementCost(BigDecimal.TEN);
            film.setRentalRate(BigDecimal.ZERO);
            film.setDescription("This is a test film");
            film.setTitle("test film");
            film.setRentalDuration((byte) 77);
            film.setOriginalLanguage(language);
            film.setCategories(new HashSet<>(categories));
            film.setYear(Year.now());
            filmDAO.save(film);

            FilmText filmText = new FilmText();
            filmText.setFilm(film);
            filmText.setDescription("This is a test film");
            filmText.setTitle("test film");
            filmText.setId(film.getId());
            filmTextDAO.save(filmText);

            transaction.commit();
            logger.info("New film '{}' created successfully with ID {}", film.getTitle(), film.getId());
        } catch (ServiceException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("ServiceException occurred: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Unexpected exception occurred", e);
            throw new ServiceException("Failed to create new film", e);
        }
    }
}
