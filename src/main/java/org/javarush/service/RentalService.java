package org.javarush.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.javarush.dao.*;
import org.javarush.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalService {
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
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Rental rental = rentalDAO.getUnreturnedRental();
            rental.setReturnDate(LocalDateTime.now());

            rentalDAO.save(rental);

            session.getTransaction().commit();
        }
    }

    public void customerRentInventory(Customer customer) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Film film = filmDAO.getAvailableFilmForRent();
            Store store = storeDAO.getItems(0, 1).getFirst();

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

            session.getTransaction().commit();
        }
    }
}
