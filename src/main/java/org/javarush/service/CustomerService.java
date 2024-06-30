package org.javarush.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javarush.dao.*;
import org.javarush.entity.*;
import org.javarush.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
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
        Transaction transaction = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();

            Store store = storeDAO.getItems(0, 1)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new ServiceException("No store found"));

            City city = cityDAO.getByName("Aden");
            if (city == null) {
                throw new ServiceException("City not found");
            }

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

            transaction.commit();
            logger.info("Customer '{}' created successfully with ID {}", customer.getFirstName() + " " + customer.getLastName(), customer.getId());
            return customer;
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
            throw new ServiceException("Failed to create customer", e);
        }
    }
}
