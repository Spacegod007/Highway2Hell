/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.fontyspublisher;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * IRemotePublisherForDomain. Interface to remote publisher in order to register 
 * and unregister properties, and to inform (remote) property listeners about 
 * changes of registered properties. In case a property changes in the domain, 
 * all listeners subscribed to that property will be informed through a 
 * (remote) method invocation of propertyChange(). Listeners subscribed to the 
 * null-String are by definition subscribed to all properties.
 * 
 * @author Frank Peeters, Nico Kuijpers
 */
public interface IRemotePublisherForDomain extends Remote {
    
    /**
     * Register property. Register property at this publisher. From now on
     * listeners can subscribe to this property. Nothing changes in case given
     * property was already registered. 
     *
     * @param property empty string not allowed
     * @throws java.rmi.RemoteException
     */
    void registerProperty(String property) throws RemoteException;
    
    /**
     * Unregister property. Unregister property at this publisher. From now on
     * listeners subscribed to this property will not be informed on changes.
     * In case given property is null-String, all properties (except null) will
     * be unregistered.
     *
     * @param property registered property at this publisher
     * @throws java.rmi.RemoteException
     */
    void unregisterProperty(String property) throws RemoteException;
    
    /**
     * Inform all listeners subscribed to property. All listeners subscribed
     * to given property as well as all listeners subscribed to null-String
     * are informed of a change of given property through a (remote) method 
     * invocation of propertyChange(). In case given property is the null-String
     * all subscribed listeners are informed.
     *
     * @param property property is either null-String or is registered
     * @param oldValue original value of property at domain (null is allowed)
     * @param newValue new value of property at domain
     * @throws java.rmi.RemoteException
     */
    void inform(String property, Object oldValue, Object newValue)
            throws RemoteException;
    
    /**
     * Obtain all registered properties. An unmodifiable list all properties
     * including the null property is returned.
     *
     * @return list of registered properties including null
     * @throws java.rmi.RemoteException
     */
    List<String> getProperties() throws RemoteException;
}
