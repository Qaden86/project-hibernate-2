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
import java.time.LocalDateTime;

public class RentalService {
    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);
    private final SessionFactory sessionFactory;
    private final RentalDAO rentalDAO;
    private final PaymentDAO paymentDAO;
    private final StoreDAO storeDAO;
    private final InventoryDAO inventoryDAO;
    private final FilmDAO filmDAO;

    public RentalService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.rentalDAO = new RentalDAO(sessionFactory);
        this.paymentDAO = new PaymentDAO(sessionFactory);
        this.storeDAO = new StoreDAO(sessionFactory);
        this.inventoryDAO = new InventoryDAO(sessionFactory);
        this.filmDAO = new FilmDAO(sessionFactory);
    }

    public void customerReturnFilm() {
        Transaction transaction = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();

            Rental rental = rentalDAO.getUnreturnedRental();
            if (rental == null) {
                throw new ServiceException("No unreturned rental found");
            }

            rental.setReturnDate(LocalDateTime.now());
            rentalDAO.save(rental);

            transaction.commit();
            logger.info("Rental {} returned successfully", rental.getId());
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
            throw new ServiceException("Failed to return rental", e);
        }
    }

    public void customerRentInventory(Customer customer) {
        Transaction transaction = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();

            Film film = filmDAO.getAvailableFilmForRent();
            if (film == null) {
                throw new ServiceException("No available film for rent");
            }

            Store store = storeDAO.getItems(0, 1)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new ServiceException("No store found"));

            Inventory inventory = new Inventory();
            inventory.setFilm(film);
            inventory.setStore(store);
            inventoryDAO.save(inventory);

            Staff staff = store.getStaff();

            Rental rental = new Rental();
            rental.setCustomer(customer);
            rental.setRentalDate(LocalDateTime.now());
            rental.setInventory(inventory);
            rental.setStaff(staff);
            rentalDAO.save(rental);

            Payment payment = new Payment();
            payment.setCustomer(customer);
            payment.setRental(rental);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setAmount(BigDecimal.valueOf(11.11));
            payment.setStaff(staff);
            paymentDAO.save(payment);

            transaction.commit();
            logger.info("Rental {} created successfully for customer {}", rental.getId(), customer.getId());
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
            throw new ServiceException("Failed to rent inventory", e);
        }
    }
}
