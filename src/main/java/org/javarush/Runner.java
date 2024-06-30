package org.javarush;

import org.hibernate.SessionFactory;
import org.javarush.config.HibernateUtil;
import org.javarush.entity.Customer;
import org.javarush.service.CustomerService;
import org.javarush.service.FilmService;
import org.javarush.service.RentalService;

public class Runner {

    private final CustomerService customerService;
    private final RentalService rentalService;
    private final FilmService filmService;

    public Runner() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        customerService = new CustomerService(sessionFactory);
        rentalService = new RentalService(sessionFactory);
        filmService = new FilmService(sessionFactory);
    }

    public void run() {
        Customer customer = customerService.createCustomer();

        rentalService.customerReturnFilm();
        rentalService.customerRentInventory(customer);
        filmService.newFilmWasMade();
    }

    public static void main(String[] args) {
        new Runner().run();
    }
}
