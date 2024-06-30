package org.javarush.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.javarush.dao.*;
import org.javarush.entity.*;

public class CustomerService {
    private final SessionFactory sessionFactory;
    private final StoreDAO storeDAO;
    private final CityDAO cityDAO;
    private final AddressDAO addressDAO;
    private final CustomerDAO customerDAO;

    public CustomerService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.storeDAO = new StoreDAO(sessionFactory);
        this.cityDAO = new CityDAO(sessionFactory);
        this.addressDAO = new AddressDAO(sessionFactory);
        this.customerDAO = new CustomerDAO(sessionFactory);
    }

    public Customer createCustomer() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Store store = storeDAO.getItems(0, 1).getFirst();

            City city = cityDAO.getByName("Aden");

            Address address = new Address();
            address.setAddress("New York");
            address.setPhone("7777777");
            address.setCity(city);
            address.setDistrict("District");
            addressDAO.save(address);

            Customer customer = new Customer();
            customer.setFirstName("John");
            customer.setLastName("Smith");
            customer.setEmail("john.smith@gmail.com");
            customer.setActive(true);
            customer.setStore(store);
            customer.setAddress(address);
            customerDAO.save(customer);

            session.getTransaction().commit();
            return customer;
        }
    }
}
