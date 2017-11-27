/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.fontyspublisher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * RemotePublisher. Remote version of Publisher.
 * 
 * @author Frank Peeters, Nico Kuijpers
 */
public class RemotePublisher extends UnicastRemoteObject 
    implements IRemotePublisherForListener, IRemotePublisherForDomain {

    // Local publisher
    Publisher publisher;
    
    /**
     * Default no-arg constructor for RemotePublisher.
     * 
     * @throws java.rmi.RemoteException
     */
    public RemotePublisher() throws RemoteException {
        publisher = new Publisher();
    }
    
    /**
     * Constructor for RemotePublisher. Property listeners may subscribe to given properties.
     * 
     * @param properties
     * @throws java.rmi.RemoteException
     */
    public RemotePublisher(String[] properties) throws RemoteException {
        publisher = new Publisher(properties);
    }

    @Override
    public void subscribeRemoteListener(IRemotePropertyListener listener, String property)
            throws RemoteException {
        publisher.subscribeRemoteListener(listener, property);
    }
    
    /**
     * Subscribe local property listener. Local listener will be subscribed to
     * given property. In case given property is the null-String, the listener
     * will be subscribed to all properties.
     *
     * @param listener local property listener to be subscribed
     * @param property null-String allowed
     */
    public void subscribeLocalListener(ILocalPropertyListener listener, String property) {

        // Subscribe local property listener
        publisher.subscribeLocalListener(listener, property);
    }
    
    @Override
    public void unsubscribeRemoteListener(IRemotePropertyListener listener, String property)
            throws RemoteException {
        publisher.unsubscribeRemoteListener(listener, property);
    }
    
    /**
     * Unsubscribe local property listener. Listener will be unsubscribed from 
     * given property. In case given property is the null-string, the listener 
     * will be unsubscribed from all properties.
     *
     * @param listener property listener to be unsubscribed
     * @param property null-String allowed
     */
    public void unsubscribeLocalListener(ILocalPropertyListener listener, String property) {
        publisher.unsubscribeLocalListener(listener, property);
    }

    @Override
    public void registerProperty(String property) throws RemoteException {
        publisher.registerProperty(property);
    }

    @Override
    public void unregisterProperty(String property) throws RemoteException {
        publisher.unregisterProperty(property);
    }

    @Override
    public void inform(String property, Object oldValue, Object newValue)
            throws RemoteException {
        publisher.inform(property, oldValue, newValue);
    }

    @Override
    public List<String> getProperties() throws RemoteException {
        return publisher.getProperties();
    }
}
